package com.quizbiblico.loja;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import com.quizbiblico.modelo.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Loja implements PurchasesUpdatedListener {

    public static final String NIVEL_3 = "nivel_3";
    public static final String NIVEL_4 = "nivel_4";
    public static final String NIVEL_5 = "nivel_5";
    public static final String NIVEL_6 = "nivel_6";
    public static final String VIP = "vip";

    private static final String[] PRODUTOS = { NIVEL_3, NIVEL_4, NIVEL_5, NIVEL_6, VIP };

    public static String idDoNivel(int codigo) {
        return "nivel_" + codigo;
    }

    private final BillingClient cliente;
    private final Usuario usuario;
    private final Runnable aoLiberar;
    private Runnable aoMudarTela;
    private boolean conectada = false;
    private final Map<String, ProductDetails> produtos = new ConcurrentHashMap<>();

    public Loja(Context context, Usuario usuario, Runnable aoLiberar) {
        this.usuario = usuario;
        this.aoLiberar = aoLiberar;
        this.cliente = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases(
                        PendingPurchasesParams.newBuilder()
                                .enableOneTimeProducts()
                                .build())
                .build();
    }

    public void conectar() {
        cliente.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult resultado) {
                conectada = resultado.getResponseCode() == BillingClient.BillingResponseCode.OK;
                if (conectada) {
                    buscarProdutos();
                    restaurarCompras();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                conectada = false;
            }
        });
    }

    public boolean isConectada() {
        return conectada;
    }

    public void setAoMudarTela(Runnable acao) {
        this.aoMudarTela = acao;
    }

    private void buscarProdutos() {
        List<QueryProductDetailsParams.Product> pedidos = new ArrayList<>();
        for (String id : PRODUTOS) {
            pedidos.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(id)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build());
        }

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(pedidos)
                .build();

        cliente.queryProductDetailsAsync(params, (resultado, resposta) -> {
            if (resultado.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                return;
            }
            for (ProductDetails ficha : resposta.getProductDetailsList()) {
                produtos.put(ficha.getProductId(), ficha);
            }
            avisarTela();
        });
    }

    public String precoDe(String id) {
        ProductDetails ficha = produtos.get(id);
        if (ficha == null || ficha.getOneTimePurchaseOfferDetails() == null) {
            return null;
        }
        return ficha.getOneTimePurchaseOfferDetails().getFormattedPrice();
    }

    public boolean comprar(Activity tela, String id) {
        ProductDetails ficha = produtos.get(id);
        if (!conectada || ficha == null) {
            return false;
        }

        BillingFlowParams.ProductDetailsParams item =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(ficha)
                        .build();

        BillingFlowParams params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(item))
                .build();

        BillingResult resultado = cliente.launchBillingFlow(tela, params);
        return resultado.getResponseCode() == BillingClient.BillingResponseCode.OK;
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult resultado, @Nullable List<Purchase> compras) {
        if (resultado.getResponseCode() != BillingClient.BillingResponseCode.OK || compras == null) {
            return;
        }
        for (Purchase compra : compras) {
            processar(compra);
        }
    }

    private void restaurarCompras() {
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build();

        cliente.queryPurchasesAsync(params, (resultado, compras) -> {
            if (resultado.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                return;
            }
            for (Purchase compra : compras) {
                processar(compra);
            }
        });
    }

    private void processar(Purchase compra) {
        if (compra.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
            return;
        }
        liberar(compra.getProducts());
        if (!compra.isAcknowledged()) {
            confirmar(compra);
        }
    }

    private void liberar(List<String> ids) {
        for (String id : ids) {
            if (VIP.equals(id)) {
                usuario.ativarVip();
            } else if (id.startsWith("nivel_")) {
                usuario.comprarNivel(Integer.parseInt(id.substring(6)));
            }
        }
        aoLiberar.run();
        avisarTela();
    }

    private void confirmar(Purchase compra) {
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(compra.getPurchaseToken())
                .build();
        cliente.acknowledgePurchase(params, resultado -> { });
    }

    private void avisarTela() {
        Runnable acao = aoMudarTela;
        if (acao != null) {
            acao.run();
        }
    }
}