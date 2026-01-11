package distribution;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DistributionService {

    private final DistributionRepository repository;
    private final InventoryServiceClient inventoryServiceClient;

    public DistributionService(
            DistributionRepository repository,
            InventoryServiceClient inventoryServiceClient
    ) {
        this.repository = repository;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    // =========================
    // CREATE
    // =========================
    public Distribution create(Distribution d) {

        // Validasi dasar
        if (d.getIdProduk() == null) {
            throw new IllegalArgumentException("idProduk wajib diisi");
        }

        if (d.getJumlahKirim() == null || d.getJumlahKirim() <= 0) {
            throw new IllegalArgumentException("jumlahKirim harus lebih dari 0");
        }

        if (d.getStatusPengiriman() == null || d.getStatusPengiriman().isEmpty()) {
            d.setStatusPengiriman("DIKIRIM");
        }

        // Kurangi stok di inventory service
        try {
            inventoryServiceClient.decreaseStock(d.getIdProduk(), d.getJumlahKirim());
        } catch (RuntimeException e) {
            // Jika gagal mengurangi stok, jangan buat distribution
            throw new IllegalArgumentException("Gagal membuat distribution: " + e.getMessage());
        }

        // ❌ JANGAN generate ID manual
        // MongoDB akan generate otomatis (_id)

        return repository.save(d);
    }

    // =========================
    // READ ALL
    // =========================
    public List<Distribution> findAll() {
        return repository.findAll();
    }

    // =========================
    // READ BY ID
    // =========================
    public Distribution findById(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Distribution dengan id " + id + " tidak ditemukan")
                );
    }

    // =========================
    // UPDATE
    // =========================
    public Distribution update(String id, Distribution data) {
        Distribution d = findById(id);

        if (data.getIdProduk() != null) {
            d.setIdProduk(data.getIdProduk());
        }

        if (data.getJumlahKirim() != null && data.getJumlahKirim() > 0) {
            d.setJumlahKirim(data.getJumlahKirim());
        }

        if (data.getTujuan() != null) {
            d.setTujuan(data.getTujuan());
        }

        if (data.getStatusPengiriman() != null) {
            d.setStatusPengiriman(data.getStatusPengiriman());
        }

        return repository.save(d);
    }

    // =========================
    // DELETE
    // =========================
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Distribution dengan id " + id + " tidak ditemukan");
        }
        repository.deleteById(id);
    }

    // =========================
    // EXTRA (OPSIONAL)
    // =========================
    public List<Distribution> findByIdProduk(Long idProduk) {
        return repository.findByIdProduk(idProduk);
    }

    public List<Distribution> findByStatus(String status) {
        return repository.findByStatusPengiriman(status);
    }
}
