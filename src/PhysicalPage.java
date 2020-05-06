
public class PhysicalPage {
	
	private long index;
	private long address;
	private long block;
	private long VID;
	
	public PhysicalPage(long index, long address, long block) {
		this.index = index;
		this.address = address;
		this.block = block;
	}
	
	public long getIndex() {
		return this.index;
	}
	
	public void setIndex(long index) {
		this.index = index;
	}
	
	public long getAddress() {
		return this.address;
	}
	
	public void setAddress(long address) {
		this.address = address;
	}
	
	public long getBlock() {
		return this.block;
	}
	
	public void setBlock(long block) {
		this.block = block;
	}

	public long getVID() {
		return VID;
	}

	public void setVID(long vID) {
		VID = vID;
	}
	
}
