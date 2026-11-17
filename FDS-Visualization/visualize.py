from pathlib import Path
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import pandas as pd
OUT=Path(__file__).resolve().parent/"output"
OUT.mkdir(parents=True,exist_ok=True)
def create_visualizations(data):
 items=data["order_items"].copy();orders=data["orders"].copy()
 if not items.empty:
  popular=items.groupby("item_name",as_index=False)["quantity"].sum().sort_values("quantity",ascending=False)
  plt.figure(figsize=(9,5));plt.bar(popular.item_name,popular.quantity)
  plt.title("Popular Menu Items");plt.xlabel("Menu item");plt.ylabel("Quantity sold")
  plt.xticks(rotation=35,ha="right");plt.tight_layout();plt.savefig(OUT/"popular_menu_items.png",dpi=150);plt.close()
  print("Saved popular_menu_items.png")
 else: print("No order-item data; popular-item chart skipped.")
 if not orders.empty and orders["status"].notna().any():
  counts=orders["status"].value_counts()
  plt.figure(figsize=(7,7));plt.pie(counts.values,labels=counts.index,autopct="%1.1f%%",startangle=90)
  plt.title("Order Status Distribution");plt.tight_layout();plt.savefig(OUT/"order_status_distribution.png",dpi=150);plt.close()
  print("Saved order_status_distribution.png")
 else: print("No order data; order-status chart skipped.")
 if not orders.empty:
  orders["order_date"]=pd.to_datetime(orders["order_date"],errors="coerce")
  done=orders[orders.status=="Completed"].dropna(subset=["order_date"])
  if not done.empty:
   daily=done.assign(day=done.order_date.dt.date).groupby("day",as_index=False).total_amount.sum()
   plt.figure(figsize=(9,5));plt.plot(daily.day.astype(str),daily.total_amount,marker="o")
   plt.title("Daily Completed Sales");plt.xlabel("Date");plt.ylabel("Sales amount")
   plt.xticks(rotation=35,ha="right");plt.tight_layout();plt.savefig(OUT/"daily_sales.png",dpi=150);plt.close()
   print("Saved daily_sales.png")
  else: print("No completed orders; daily-sales chart skipped.")
 else: print("No orders; daily-sales chart skipped.")
if __name__=="__main__": print("Run FDS-Python/run_pipeline.py to retrieve data before plotting.")
