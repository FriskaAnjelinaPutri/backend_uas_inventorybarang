import mysql.connector

def get_db():
    return mysql.connector.connect(
        host="mysql-inventory",
        user="root",
        password="root",
        database="inventory_db"
    )
