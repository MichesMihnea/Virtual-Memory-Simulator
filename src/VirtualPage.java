
public class VirtualPage {
	private long id;
	private long physicalPage;
	private long loc;
	private boolean valid;
	private long block;
	
	public VirtualPage(long id, long physicalPage) {
		this.id = id;
		this.physicalPage = physicalPage;
		this.valid = false;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getPhysicalPage() {
		return this.physicalPage;
	}
	
	public void setPhysicalPage(long physicalPage) {
		this.physicalPage = physicalPage;
	}
	
	public boolean getValid() {
		return this.valid;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public long getLoc() {
		return this.loc;
	}
	
	public void setLoc(long loc) {
		this.loc = loc;
	}
	
	public String toString() {
		return Long.toString(this.id);
	}

	public long getBlock() {
		return block;
	}

	public void setBlock(long block) {
		this.block = block;
	}
}
