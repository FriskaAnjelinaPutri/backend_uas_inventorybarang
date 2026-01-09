const express = require("express");
const router = express.Router();
const pool = require("../db");

// CREATE product
router.post("/", async (req, res) => {
  try {
    const { nama_produk, kategori, merek, harga, spesifikasi } = req.body;
    const result = await pool.query(
      "INSERT INTO products (nama_produk, kategori, merek, harga, spesifikasi) VALUES ($1,$2,$3,$4,$5) RETURNING *",
      [nama_produk, kategori, merek, harga, spesifikasi]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Internal server error" });
  }
});

// READ all products
router.get("/", async (req, res) => {
  try {
    const result = await pool.query("SELECT * FROM products");
    res.json(result.rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Internal server error" });
  }
});

// READ by ID
router.get("/:id", async (req, res) => {
  try {
    const result = await pool.query(
      "SELECT * FROM products WHERE id_produk=$1",
      [req.params.id]
    );
    if (!result.rows[0]) return res.status(404).json({ error: "Product not found" });
    res.json(result.rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Internal server error" });
  }
});

// UPDATE by ID
router.put("/:id", async (req, res) => {
  try {
    const { nama_produk, kategori, merek, harga, spesifikasi } = req.body;
    const result = await pool.query(
      "UPDATE products SET nama_produk=$1, kategori=$2, merek=$3, harga=$4, spesifikasi=$5 WHERE id_produk=$6 RETURNING *",
      [nama_produk, kategori, merek, harga, spesifikasi, req.params.id]
    );
    if (!result.rows[0]) return res.status(404).json({ error: "Product not found" });
    res.json(result.rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Internal server error" });
  }
});

// DELETE by ID
router.delete("/:id", async (req, res) => {
  try {
    const result = await pool.query(
      "DELETE FROM products WHERE id_produk=$1 RETURNING *",
      [req.params.id]
    );
    if (!result.rows[0]) return res.status(404).json({ error: "Product not found" });
    res.json({ message: "Product deleted", product: result.rows[0] });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Internal server error" });
  }
});

module.exports = router;
