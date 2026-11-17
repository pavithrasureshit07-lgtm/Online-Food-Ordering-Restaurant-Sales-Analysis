import os
DB_CONFIG = {
 "host": os.getenv("DB_HOST","localhost"),
 "port": int(os.getenv("DB_PORT","3306")),
 "database": os.getenv("DB_NAME","restaurant_db"),
 "user": os.getenv("DB_USER","root"),
 "password": os.getenv("DB_PASSWORD",""),
}
