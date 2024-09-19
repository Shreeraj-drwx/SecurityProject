import requests
import mysql.connector

def create_connection():
    try:
        connection = mysql.connector.connect(
            host='localhost',
            user='newuser',
            password='password',
            database='StockManager'
        )
        return connection
    except mysql.connector.Error as e:
        print(f"Error connecting to the database: {e}")
        return None

def clear_table():
    connection = create_connection()
    if connection:
        cursor = connection.cursor()

        cursor.execute("DELETE FROM asset_prices")

        connection.commit()
        print("Cleared all rows from asset_prices table.")
        connection.close()

def fetch_symbols_from_database():
    connection = create_connection()
    if connection:
        cursor = connection.cursor()
        cursor.execute("SELECT DISTINCT symbol FROM assets")
        symbols = [row[0] for row in cursor.fetchall()]
        connection.close()
        return symbols
    else:
        return []

def fetch_stock_data(symbol):
    url = f'https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={symbol}&apikey=1NHH6YOD4QEG3969'
    r = requests.get(url)
    data = r.json()
    return data

def update_database(symbol, price):
    connection = create_connection()
    if connection:
        cursor = connection.cursor()

        # Insert new data
        cursor.execute("INSERT INTO asset_prices (symbol, market_price) VALUES (%s, %s)", (symbol, price))

        connection.commit()
        print(f"Data for {symbol} successfully updated in the database.")
        connection.close()

def main():
    clear_table()

    symbols = fetch_symbols_from_database()

    for symbol in symbols:
        data = fetch_stock_data(symbol)

        if 'Global Quote' in data:
            symbol = data['Global Quote']['01. symbol']
            price = data['Global Quote']['05. price']

            update_database(symbol, price)
        else:
            print(f"Failed to retrieve data for {symbol}")

if __name__ == "__main__":
    main()
