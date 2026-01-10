from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.database import get_db

router = APIRouter()

# =====================
# SCHEMA
# =====================
class Inventory(BaseModel):
    id_produk: int
    jumlah_stok: int
    stok_minimum: int
    lokasi_gudang: str | None = None


# =====================
# CREATE
# =====================
@router.post("/inventory")
def create_inventory(data: Inventory):
    db = get_db()
    cursor = db.cursor(dictionary=True)

    query = """
        INSERT INTO inventory
        (id_produk, jumlah_stok, stok_minimum, lokasi_gudang)
        VALUES (%s, %s, %s, %s)
    """

    cursor.execute(query, (
        data.id_produk,
        data.jumlah_stok,
        data.stok_minimum,
        data.lokasi_gudang
    ))

    db.commit()
    new_id = cursor.lastrowid
    
    # data yang baru ditambahkan
    cursor.execute("SELECT * FROM inventory WHERE id=%s", (new_id,))
    new_data = cursor.fetchone()
    
    cursor.close()
    db.close()

    return {
        "message": "Data inventory berhasil ditambahkan",
        "data": new_data
    }


# =====================
# READ ALL
# =====================
@router.get("/inventory")
def get_all_inventory():
    db = get_db()
    cursor = db.cursor(dictionary=True)

    cursor.execute("SELECT * FROM inventory")
    data = cursor.fetchall()

    cursor.close()
    db.close()

    return data


# =====================
# READ BY ID
# =====================
@router.get("/inventory/{id}")
def get_inventory(id: int):
    db = get_db()
    cursor = db.cursor(dictionary=True)

    cursor.execute("SELECT * FROM inventory WHERE id=%s", (id,))
    data = cursor.fetchone()

    cursor.close()
    db.close()

    if not data:
        raise HTTPException(status_code=404, detail="Data tidak ditemukan")

    return data


# =====================
# UPDATE
# =====================
@router.put("/inventory/{id}")
def update_inventory(id: int, data: Inventory):
    db = get_db()
    cursor = db.cursor(dictionary=True)

    query = """
        UPDATE inventory
        SET id_produk=%s,
            jumlah_stok=%s,
            stok_minimum=%s,
            lokasi_gudang=%s
        WHERE id=%s
    """

    cursor.execute(query, (
        data.id_produk,
        data.jumlah_stok,
        data.stok_minimum,
        data.lokasi_gudang,
        id
    ))

    db.commit()

    if cursor.rowcount == 0:
        raise HTTPException(status_code=404, detail="Data tidak ditemukan")

    # data yang sudah diupdate
    cursor.execute("SELECT * FROM inventory WHERE id=%s", (id,))
    updated_data = cursor.fetchone()
    
    cursor.close()
    db.close()

    return {
        "message": "Data inventory berhasil diupdate",
        "data": updated_data
    }


# =====================
# DELETE
# =====================
@router.delete("/inventory/{id}")
def delete_inventory(id: int):
    db = get_db()
    cursor = db.cursor()

    cursor.execute("DELETE FROM inventory WHERE id=%s", (id,))
    db.commit()

    if cursor.rowcount == 0:
        raise HTTPException(status_code=404, detail="Data tidak ditemukan")

    cursor.close()
    db.close()

    return {"message": "Data inventory berhasil dihapus"}
