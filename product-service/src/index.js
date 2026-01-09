const express = require("express");
const cors = require("cors");
require("dotenv").config();

const productRoutes = require("./routes/product.routes");

const app = express();
app.use(cors());
app.use(express.json());

app.use("/products", productRoutes);
const pool = require("./db");

async function initDb() {
  const createTableQuery = `
    CREATE TABLE IF NOT EXISTS products (
      id_produk SERIAL PRIMARY KEY,
      nama_produk VARCHAR(255) NOT NULL,
      kategori VARCHAR(100),
      merek VARCHAR(100),
      harga NUMERIC(12,2),
      spesifikasi TEXT,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `;
  await pool.query(createTableQuery);
}

initDb()
  .then(() => {
    app.listen(process.env.PORT, () => {
      console.log(`Product Service running on port ${process.env.PORT}`);
    });
  })
  .catch((err) => {
    console.error("Failed to initialize database:", err);
    process.exit(1);
  });
