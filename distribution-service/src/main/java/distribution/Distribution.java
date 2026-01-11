package distribution;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "distributions")
public class Distribution {

    @Id
    private String id;

    @Field("id_produk")
    private Long idProduk;

    @Field("jumlah_kirim")
    private Integer jumlahKirim;

    private String tujuan;

    @Field("status_pengiriman")
    private String statusPengiriman;

    @Field("tanggal_kirim")
    private LocalDateTime tanggalKirim;

    // =====================
    // CONSTRUCTOR
    // =====================
    public Distribution() {
        this.tanggalKirim = LocalDateTime.now();
        this.statusPengiriman = "DIKIRIM";
    }

    // =====================
    // GETTER
    // =====================
    public String getId() {
        return id;
    }

    public Long getIdProduk() {
        return idProduk;
    }

    public Integer getJumlahKirim() {
        return jumlahKirim;
    }

    public String getTujuan() {
        return tujuan;
    }

    public String getStatusPengiriman() {
        return statusPengiriman;
    }

    public LocalDateTime getTanggalKirim() {
        return tanggalKirim;
    }

    // =====================
    // SETTER
    // =====================
    public void setId(String id) {
        this.id = id;
    }

    public void setIdProduk(Long idProduk) {
        this.idProduk = idProduk;
    }

    public void setJumlahKirim(Integer jumlahKirim) {
        this.jumlahKirim = jumlahKirim;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public void setStatusPengiriman(String statusPengiriman) {
        this.statusPengiriman = statusPengiriman;
    }

    public void setTanggalKirim(LocalDateTime tanggalKirim) {
        this.tanggalKirim = tanggalKirim;
    }
}
