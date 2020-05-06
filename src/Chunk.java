
public class Chunk {
	private String name;
	private long size;
	private long start;
	private long end;
	
	public Chunk(String name, long size) {
		this.name = name;
		this.size = size;
	}
	
	public String getName() {
		return this.name;
	}
	
	public long getSize() {
		return this.size;
	}
	
	public long getStart() {
		return this.start;
	}
	
	public long getEnd() {
		return this.end;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
	public void setStart(long start) {
		this.start = start;
	}
	
	public void setEnd(long end) {
		this.end = end;
	}
	
	public String toString() {
		return "'\"" + this.name + "'\" " + this.size;
	}

}
