package distribution;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributionRepository extends MongoRepository<Distribution, String> {

    // Cari distribusi berdasarkan ID Produk
    List<Distribution> findByIdProduk(Long idProduk);

    // Cari distribusi berdasarkan status pengiriman
    List<Distribution> findByStatusPengiriman(String statusPengiriman);

    // Cari distribusi berdasarkan tujuan
    List<Distribution> findByTujuan(String tujuan);
}
