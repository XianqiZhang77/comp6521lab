package comp6521.lab.com.Records;

public abstract class Record {
	public static int GetRecordSize() { return 0; }
	public abstract void Parse(String data);
	public abstract String Write();
}