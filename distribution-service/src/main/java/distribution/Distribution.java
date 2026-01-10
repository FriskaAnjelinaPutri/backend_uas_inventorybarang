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
    private Integer jumlahKirim;
    private String tujuan;
    private String statusPengiriman;
    private LocalDateTime tanggalKirim = LocalDateTime.now();

    public Distribution() {}

    public String getId() { return id; }
    public Long getIdProduk() { return idProduk; }
    public Integer getJumlahKirim() { return jumlahKirim; }
    public String getTujuan() { return tujuan; }
    public String getStatusPengiriman() { return statusPengiriman; }
    public LocalDateTime getTanggalKirim() { return tanggalKirim; }

    public void setId(String id) { this.id = id; }
    public void setIdProduk(Long idProduk) { this.idProduk = idProduk; }
    public void setJumlahKirim(Integer jumlahKirim) { this.jumlahKirim = jumlahKirim; }
    public void setTujuan(String tujuan) { this.tujuan = tujuan; }
    public void setStatusPengiriman(String statusPengiriman) { this.statusPengiriman = statusPengiriman; }
    public void setTanggalKirim(LocalDateTime tanggalKirim) { this.tanggalKirim = tanggalKirim; }
}
