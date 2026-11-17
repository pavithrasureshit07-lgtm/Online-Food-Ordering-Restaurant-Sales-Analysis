from pathlib import Path
import sys
from retrieve_data import retrieve_all
from preprocess import clean_data
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/"FDS-Visualization"))
from visualize import create_visualizations
def main():
 print("Retrieving actual restaurant database records...")
 raw=retrieve_all()
 print("Preprocessing records...")
 cleaned=clean_data(raw)
 print("Creating visualizations...")
 create_visualizations(cleaned)
 print("Pipeline finished. Check FDS-Python/output and FDS-Visualization/output.")
if __name__=="__main__": main()
