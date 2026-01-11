package distribution;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Component
public class InventoryServiceClient {

    private final WebClient webClient;
    private final String inventoryServiceUrl;

    public InventoryServiceClient(
            WebClient.Builder webClientBuilder,
            @Value("${inventory.service.url}") String inventoryServiceUrl
    ) {
        this.inventoryServiceUrl = inventoryServiceUrl;
        this.webClient = webClientBuilder.baseUrl(inventoryServiceUrl).build();
    }

    /**
     * Mengurangi stok inventory berdasarkan id_produk
     * @param idProduk ID produk yang stoknya akan dikurangi
     * @param jumlah Jumlah stok yang akan dikurangi
     * @throws RuntimeException jika stok tidak cukup atau produk tidak ditemukan
     */
    public void decreaseStock(Long idProduk, Integer jumlah) {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("jumlah", jumlah);

        try {
            webClient.patch()
                    .uri("/inventory/product/{id_produk}/decrease", idProduk)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // Synchronous call
        } catch (WebClientResponseException e) {
            String errorMessage = e.getResponseBodyAsString();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = e.getMessage();
            }

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Produk dengan id " + idProduk + " tidak ditemukan di inventory: " + errorMessage);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Gagal mengurangi stok: " + errorMessage);
            } else {
                throw new RuntimeException("Error saat memanggil inventory service: " + errorMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saat memanggil inventory service: " + e.getMessage(), e);
        }
    }
}
