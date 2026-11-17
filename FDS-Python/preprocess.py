from pathlib import Path
import pandas as pd
OUT=Path(__file__).resolve().parent/"output"
OUT.mkdir(parents=True,exist_ok=True)
def clean_data(data):
 cleaned={}
 for name,frame in data.items():
  df=frame.copy()
  before=len(df);df=df.drop_duplicates();removed=before-len(df)
  for col in df.select_dtypes(include=["object"]).columns:
   df[col]=df[col].map(lambda x:x.strip() if isinstance(x,str) else x)
  for col in df.select_dtypes(include=["number"]).columns:
   if df[col].isna().any():
    median=df[col].median();df[col]=df[col].fillna(0 if pd.isna(median) else median)
  for col in ("order_date","payment_date"):
   if col in df.columns: df[col]=pd.to_datetime(df[col],errors="coerce")
  if name=="orders" and "order_date" in df.columns:
   df["total_amount"]=pd.to_numeric(df["total_amount"],errors="coerce").fillna(0)
   df["order_day"]=df["order_date"].dt.date
  if name=="order_items":
   for col in ("quantity","unit_price","line_total"):
    df[col]=pd.to_numeric(df[col],errors="coerce").fillna(0)
   df["calculated_line_total"]=df["quantity"]*df["unit_price"]
  cleaned[name]=df;df.to_csv(OUT/f"{name}_cleaned.csv",index=False)
  print(f"{name}: {removed} duplicate(s) removed; {len(df)} row(s) remain.")
 return cleaned
if __name__=="__main__":
 from retrieve_data import retrieve_all
 clean_data(retrieve_all())
