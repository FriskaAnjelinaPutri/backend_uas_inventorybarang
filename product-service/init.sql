CREATE TABLE IF NOT EXISTS products (
    id_produk SERIAL PRIMARY KEY,
    nama_produk VARCHAR(100) NOT NULL,
    kategori VARCHAR(50) NOT NULL,
    merek VARCHAR(50) NOT NULL,
    harga INTEGER NOT NULL,
    spesifikasi TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
