import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.SpringLayout;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.synth.SynthSpinnerUI;
import javax.swing.table.DefaultTableModel;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Label;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Rectangle;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainGUI extends JFrame{
	private JTable diskTable;
	private JTable TLB;
	private JTable pageTable;
	private JTable memTable;
	private JTable appTable;
	private AddGUI aGUI;
	private ReadGUI lGUI;
	private WriteGUI wGUI;
	private long TLBSize = 10;
	private long memSize = 256;
	private long pageSize = 128;
	private long currCapacity = 0;
	private List <String> disk;
	private List <VirtualPage> TLBEntries;
	private List <VirtualPage> pages;
	private List <PhysicalPage> physicalMem;
	private List <Chunk> apps;
	private JTextField capacityField;
	private JProgressBar progressBar;
	private JTextField hitField;
	private float hitRate = 100;
	private int TLBSearches = 0;
	private int hitCount = 0;
	private int TLBRank = 0;
	private JTextArea logArea;
	private boolean movedToDisk = false;
	private int loadStep = -1;
	private VirtualPage newPage;
	private boolean skipLoad = true;
	private int memRank = -1;
	private long currID = 0;
	private PRA algorithm;
	private enum PRA {FIFO, CLK, LRU};
	private boolean clkBuff[];
	private int clkHand = 0;
	private List <LocalTime> recents;
	private String dataToWrite = null;

	public MainGUI() {
		setBounds(new Rectangle(600, 350, 1050, 530));
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		disk = new ArrayList <String> ();
		TLBEntries = new ArrayList <VirtualPage> ();
		pages = new ArrayList <VirtualPage> ();
		physicalMem = new ArrayList <PhysicalPage> ();
		apps = new ArrayList <Chunk> ();

		diskTable = new JTable(new DefaultTableModel(new Object[]{"Column1"}, getDefaultCloseOperation()));
		springLayout.putConstraint(SpringLayout.NORTH, diskTable, 65, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, diskTable, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, diskTable, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, diskTable, -870, SpringLayout.EAST, getContentPane());
		diskTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		getContentPane().add(diskTable);

		JLabel lblPrograms = new JLabel("Disk");
		springLayout.putConstraint(SpringLayout.WEST, lblPrograms, 38, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblPrograms, -459, SpringLayout.SOUTH, getContentPane());
		lblPrograms.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblPrograms);

		TLB = new JTable(new DefaultTableModel(new Object[]{"Column1", "Column2", "Column3"}, getDefaultCloseOperation()));
		springLayout.putConstraint(SpringLayout.WEST, TLB, 6, SpringLayout.EAST, diskTable);
		springLayout.putConstraint(SpringLayout.SOUTH, TLB, -10, SpringLayout.SOUTH, getContentPane());
		getContentPane().add(TLB);

		pageTable = new JTable(new DefaultTableModel(new Object[]{"Column1", "Column2"}, getDefaultCloseOperation()));
		springLayout.putConstraint(SpringLayout.NORTH, pageTable, 65, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, pageTable, 332, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, pageTable, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, pageTable, -538, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, TLB, -8, SpringLayout.WEST, pageTable);
		getContentPane().add(pageTable);

		memTable = new JTable(new DefaultTableModel(new Object[]{"Column1", "Column2", "Column3"}, getDefaultCloseOperation()));
		springLayout.putConstraint(SpringLayout.NORTH, memTable, 0, SpringLayout.NORTH, diskTable);
		springLayout.putConstraint(SpringLayout.WEST, memTable, 6, SpringLayout.EAST, pageTable);
		springLayout.putConstraint(SpringLayout.SOUTH, memTable, 0, SpringLayout.SOUTH, diskTable);
		springLayout.putConstraint(SpringLayout.EAST, memTable, -377, SpringLayout.EAST, getContentPane());
		getContentPane().add(memTable);

		JButton btnNewRequest = new JButton("Load Chunk");
		springLayout.putConstraint(SpringLayout.NORTH, btnNewRequest, -4, SpringLayout.NORTH, diskTable);
		btnNewRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainGUI.this.aGUI = new AddGUI(MainGUI.this);
			}
		});
		getContentPane().add(btnNewRequest);

		JLabel lblTlb = new JLabel("TLB");
		springLayout.putConstraint(SpringLayout.WEST, lblTlb, 224, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblPrograms, -109, SpringLayout.WEST, lblTlb);
		springLayout.putConstraint(SpringLayout.NORTH, lblTlb, 0, SpringLayout.NORTH, lblPrograms);
		lblTlb.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblTlb);

		JLabel lblPageTable = new JLabel("Page Table");
		springLayout.putConstraint(SpringLayout.NORTH, lblPageTable, 0, SpringLayout.NORTH, lblPrograms);
		springLayout.putConstraint(SpringLayout.WEST, lblPageTable, 106, SpringLayout.EAST, lblTlb);
		lblPageTable.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblPageTable);

		JLabel lblPhysicalMemory = new JLabel("Physical Memory");
		springLayout.putConstraint(SpringLayout.NORTH, lblPhysicalMemory, 0, SpringLayout.NORTH, lblPrograms);
		springLayout.putConstraint(SpringLayout.WEST, lblPhysicalMemory, 55, SpringLayout.EAST, lblPageTable);
		lblPhysicalMemory.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblPhysicalMemory);

		progressBar = new JProgressBar(0, (int) this.memSize);
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 0, SpringLayout.WEST, btnNewRequest);
		springLayout.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, btnNewRequest);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		getContentPane().add(progressBar);

		JLabel lblFillFactor = new JLabel("Fill Factor");
		springLayout.putConstraint(SpringLayout.EAST, lblFillFactor, -80, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, progressBar, 18, SpringLayout.SOUTH, lblFillFactor);
		lblFillFactor.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblFillFactor);

		JButton btnLoadPage = new JButton("Read");
		springLayout.putConstraint(SpringLayout.NORTH, btnLoadPage, 6, SpringLayout.SOUTH, btnNewRequest);
		springLayout.putConstraint(SpringLayout.WEST, btnLoadPage, 0, SpringLayout.WEST, btnNewRequest);
		springLayout.putConstraint(SpringLayout.EAST, btnLoadPage, -63, SpringLayout.EAST, getContentPane());
		btnLoadPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lGUI = new ReadGUI(MainGUI.this);
			}
		});
		getContentPane().add(btnLoadPage);

		logArea = new JTextArea();
		springLayout.putConstraint(SpringLayout.EAST, logArea, -37, SpringLayout.EAST, getContentPane());
		getContentPane().add(logArea);

		JLabel lblLog = new JLabel("Log");
		springLayout.putConstraint(SpringLayout.NORTH, logArea, 6, SpringLayout.SOUTH, lblLog);
		springLayout.putConstraint(SpringLayout.SOUTH, lblLog, -250, SpringLayout.SOUTH, getContentPane());
		lblLog.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblLog);

		capacityField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, lblFillFactor, 6, SpringLayout.SOUTH, capacityField);
		springLayout.putConstraint(SpringLayout.WEST, capacityField, 0, SpringLayout.WEST, btnNewRequest);
		springLayout.putConstraint(SpringLayout.EAST, capacityField, 0, SpringLayout.EAST, btnNewRequest);
		getContentPane().add(capacityField);
		capacityField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Capacity");
		springLayout.putConstraint(SpringLayout.NORTH, capacityField, 6, SpringLayout.SOUTH, lblNewLabel);
		springLayout.putConstraint(SpringLayout.SOUTH, lblNewLabel, -131, SpringLayout.SOUTH, getContentPane());
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblNewLabel);

		JLabel lblHitRate = new JLabel("Hit Rate");
		springLayout.putConstraint(SpringLayout.EAST, lblNewLabel, 0, SpringLayout.EAST, lblHitRate);
		springLayout.putConstraint(SpringLayout.SOUTH, lblHitRate, -316, SpringLayout.SOUTH, getContentPane());
		lblHitRate.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblHitRate);

		hitField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, hitField, 6, SpringLayout.SOUTH, lblHitRate);
		springLayout.putConstraint(SpringLayout.WEST, hitField, 0, SpringLayout.WEST, btnNewRequest);
		springLayout.putConstraint(SpringLayout.EAST, hitField, 0, SpringLayout.EAST, btnNewRequest);
		hitField.setFont(new Font("Tahoma", Font.PLAIN, 11));
		getContentPane().add(hitField);
		hitField.setColumns(10);

		JButton btnNextStep = new JButton("Next Step");
		springLayout.putConstraint(SpringLayout.SOUTH, logArea, -6, SpringLayout.NORTH, btnNextStep);
		springLayout.putConstraint(SpringLayout.SOUTH, btnNextStep, -18, SpringLayout.NORTH, lblNewLabel);
		springLayout.putConstraint(SpringLayout.EAST, btnNextStep, 0, SpringLayout.EAST, btnNewRequest);
		btnNextStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if(skipLoad) {
					System.out.println("SKIP LOAD FALSE! RETURN");
					return;
				}

				loadStep ++;

				if(loadStep > 4) {
					loadStep = -1;
					movedToDisk = false;
					return;
				}

				System.out.println("BEGINNING STEP " + loadStep);

				fulfillLoad(currID, true, loadStep);
			}
		});
		getContentPane().add(btnNextStep);

		JLabel lblIndex = new JLabel("Index");
		springLayout.putConstraint(SpringLayout.WEST, lblIndex, 62, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblIndex, -6, SpringLayout.NORTH, diskTable);
		getContentPane().add(lblIndex);

		JLabel lblVirtual = new JLabel("Virtual");
		springLayout.putConstraint(SpringLayout.SOUTH, lblVirtual, -433, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, TLB, 6, SpringLayout.SOUTH, lblVirtual);
		springLayout.putConstraint(SpringLayout.WEST, lblVirtual, 0, SpringLayout.WEST, lblTlb);
		getContentPane().add(lblVirtual);

		JLabel lblPhysical = new JLabel("Physical");
		springLayout.putConstraint(SpringLayout.NORTH, lblPhysical, 0, SpringLayout.NORTH, lblIndex);
		springLayout.putConstraint(SpringLayout.WEST, lblPhysical, 21, SpringLayout.EAST, lblVirtual);
		getContentPane().add(lblPhysical);

		JLabel lblVirtual_1 = new JLabel("Virtual");
		springLayout.putConstraint(SpringLayout.WEST, lblVirtual_1, 31, SpringLayout.EAST, lblPhysical);
		springLayout.putConstraint(SpringLayout.SOUTH, lblVirtual_1, -6, SpringLayout.NORTH, pageTable);
		getContentPane().add(lblVirtual_1);

		JLabel lblPhysical_1 = new JLabel("Physical");
		springLayout.putConstraint(SpringLayout.SOUTH, lblPhysical_1, -6, SpringLayout.NORTH, pageTable);
		getContentPane().add(lblPhysical_1);

		JLabel lblIndex_1 = new JLabel("Index");
		springLayout.putConstraint(SpringLayout.NORTH, lblIndex_1, 0, SpringLayout.NORTH, lblIndex);
		springLayout.putConstraint(SpringLayout.EAST, lblIndex_1, -25, SpringLayout.WEST, lblVirtual);
		getContentPane().add(lblIndex_1);

		JLabel lblContent = new JLabel("Content");
		springLayout.putConstraint(SpringLayout.SOUTH, lblContent, -6, SpringLayout.NORTH, memTable);
		springLayout.putConstraint(SpringLayout.EAST, lblContent, 0, SpringLayout.EAST, memTable);
		getContentPane().add(lblContent);

		JLabel lblAddress = new JLabel("Address");
		springLayout.putConstraint(SpringLayout.SOUTH, lblAddress, -6, SpringLayout.NORTH, memTable);
		springLayout.putConstraint(SpringLayout.EAST, lblAddress, -19, SpringLayout.WEST, lblContent);
		getContentPane().add(lblAddress);
		
		JLabel lblApps = new JLabel("Data Chunks");
		springLayout.putConstraint(SpringLayout.NORTH, lblApps, 0, SpringLayout.NORTH, lblPrograms);
		springLayout.putConstraint(SpringLayout.WEST, lblApps, 94, SpringLayout.EAST, lblPhysicalMemory);
		lblApps.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblApps);
		
		JLabel lblIndex_2 = new JLabel("Index");
		springLayout.putConstraint(SpringLayout.EAST, lblPhysical_1, -26, SpringLayout.WEST, lblIndex_2);
		springLayout.putConstraint(SpringLayout.WEST, lblIndex_2, 0, SpringLayout.WEST, memTable);
		springLayout.putConstraint(SpringLayout.SOUTH, lblIndex_2, -6, SpringLayout.NORTH, memTable);
		getContentPane().add(lblIndex_2);
		
		appTable = new JTable(new DefaultTableModel(new Object[]{"Column1", "Column2", "Column3"}, getDefaultCloseOperation()));
		springLayout.putConstraint(SpringLayout.WEST, lblHitRate, 58, SpringLayout.EAST, appTable);
		springLayout.putConstraint(SpringLayout.WEST, lblLog, 71, SpringLayout.EAST, appTable);
		springLayout.putConstraint(SpringLayout.WEST, logArea, 16, SpringLayout.EAST, appTable);
		springLayout.putConstraint(SpringLayout.WEST, btnNewRequest, 38, SpringLayout.EAST, appTable);
		springLayout.putConstraint(SpringLayout.EAST, appTable, -198, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, appTable, 6, SpringLayout.EAST, memTable);
		springLayout.putConstraint(SpringLayout.NORTH, appTable, 0, SpringLayout.NORTH, diskTable);
		springLayout.putConstraint(SpringLayout.SOUTH, appTable, -10, SpringLayout.SOUTH, getContentPane());
		getContentPane().add(appTable);
		
		JLabel lblName = new JLabel("Name");
		springLayout.putConstraint(SpringLayout.NORTH, lblName, 0, SpringLayout.NORTH, lblIndex);
		springLayout.putConstraint(SpringLayout.WEST, lblName, 19, SpringLayout.EAST, lblContent);
		getContentPane().add(lblName);
		
		JLabel lblStart = new JLabel("Start");
		springLayout.putConstraint(SpringLayout.NORTH, lblStart, 0, SpringLayout.NORTH, lblIndex);
		getContentPane().add(lblStart);
		
		JLabel lblEnd = new JLabel("End");
		springLayout.putConstraint(SpringLayout.WEST, lblEnd, 779, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblStart, -28, SpringLayout.WEST, lblEnd);
		springLayout.putConstraint(SpringLayout.NORTH, lblEnd, 0, SpringLayout.NORTH, lblIndex);
		getContentPane().add(lblEnd);
		
		JButton writeButton = new JButton("Write");
		springLayout.putConstraint(SpringLayout.NORTH, writeButton, 6, SpringLayout.SOUTH, btnLoadPage);
		springLayout.putConstraint(SpringLayout.WEST, writeButton, 0, SpringLayout.WEST, btnNewRequest);
		springLayout.putConstraint(SpringLayout.EAST, writeButton, 0, SpringLayout.EAST, btnNewRequest);
		writeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				wGUI = new WriteGUI(MainGUI.this);
			}
		});
		getContentPane().add(writeButton);
		
		clkBuff = new boolean[1000];
		recents = new ArrayList <LocalTime> ();
	}

	public void setTLBSize(long TLBSize) {
		this.TLBSize = TLBSize;
		
	}

	public void setMemSize(long memSize) {
		this.capacityField.setText("0 / " + Long.toString(memSize));
		progressBar.setMaximum((int) memSize);
		this.memSize = memSize;
	}

	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}
	
	public long getAppCode(Chunk newApp) {
		long code = newApp.getName().hashCode();
		
		return code;
	}

	public void fulfillRequest() {
		Chunk newApp = new Chunk(aGUI.getNameField(), aGUI.getSizeField());
		System.out.println(newApp.getName() + "     " + newApp.getSize());
		
		long start = getAppCode(newApp);
		
		this.setAlgorithm(aGUI.getAlgorithmIndex());
		
		newApp.setStart(start);
		newApp.setEnd(start + (newApp.getSize() / this.pageSize));
		
		apps.add(newApp);
		
		Iterator <Chunk> ait = apps.iterator();
		
		this.clearTable(appTable);
		
		while(ait.hasNext()) {
			this.addToTable(ait.next(), appTable);
		}
		
		long noPages = ((newApp.getSize() - 1) / this.pageSize) + 1;

		for(int i = 0; i < noPages; i ++) {
			this.fulfillLoad(newApp.getStart() + i * this.pageSize, false, 0);
		}
		//disk.add(newApp);
		//this.addToTable(newApp, diskTable);
	}

	public void updateLog(String text) {
		this.logArea.setText(text);
	}

	public void moveToDisk(int diskIndex, boolean stepByStep) {
		
		PhysicalPage pageToBeMoved = physicalMem.get(diskIndex); 
		System.out.println(pageToBeMoved.getBlock());
		VirtualPage virtPageToRemove;
		boolean onDisk = false;
		this.clearTable(diskTable);
		disk.add(Long.toString(pageToBeMoved.getVID()));

		Iterator <String> sit = disk.iterator();

		while(sit.hasNext()) {
			this.addToTable(sit.next(), diskTable);
		}

		Iterator <VirtualPage> vit = pages.iterator();

		while(vit.hasNext()) {
			virtPageToRemove = vit.next();
			if(pageToBeMoved.getVID() == virtPageToRemove.getId()) {
				virtPageToRemove.setPhysicalPage(-1);
				break;
			}
		}

		this.clearTable(memTable);
		
		//this.updateCapacity(-this.pageSize);
		this.movedToDisk = true;
		
		physicalMem.remove(diskIndex);
		recents.remove(diskIndex);

		Iterator <PhysicalPage>pit = physicalMem.iterator();
		PhysicalPage currPhysPage;

		memRank = -1;
		
		while(pit.hasNext()) {
			currPhysPage = pit.next();
			currPhysPage.setIndex(++memRank);
			this.addToTable(currPhysPage, memTable);
		}

		if(stepByStep) {
			this.updateLog("No space for the new page!\nMoving page " + pageToBeMoved.getVID() + " to disk...");
		}


		//pageToBeMoved.setPhysicalPage(-1);
		//newPage.setPhysicalPage(-1);
		this.clearTable(TLB);
		Iterator <VirtualPage> it = TLBEntries.iterator();
		while(it.hasNext()) {
			this.addToTable(it.next(), TLB);
		}
		onDisk = true;

		this.clearTable(pageTable);

		it = pages.iterator();
		VirtualPage currPage, memPageToBeMoved = new VirtualPage(0, 0);

		while(it.hasNext()) {
			currPage = it.next();
			if(currPage.getId() == pageToBeMoved.getIndex()) {
				memPageToBeMoved = currPage;
				break;
			}
		}


		it = pages.iterator();

		while(it.hasNext()) {
			this.addToTable(it.next(), pageTable);
		}
	
		
	}
	
	public void setAlgorithm(int index) {
		
		if(index == 0) {
			this.algorithm = PRA.FIFO;
		}
		
		if(index == 1) {
			this.algorithm = PRA.CLK;
			this.clkHand = 0;
		}
		
		if(index == 2) {
			this.algorithm = PRA.LRU;
		}
	}

	public void fulfillLoad(long id, boolean stepByStep, int step) {

		boolean onDisk = false;
		this.currID = id;
		
		if(this.dataToWrite != null) {
			try {
				Long.decode(this.dataToWrite);
			}catch(NumberFormatException nfex) {
		        JOptionPane.showMessageDialog(null, "Please enter valid hex input!", "Bad input!", JOptionPane.ERROR_MESSAGE);
		        return;
			}
		}

		if(!stepByStep || (stepByStep && step == -1)) {
			newPage = new VirtualPage(id, (long) (Math.random() * 10000));
			newPage.setLoc(newPage.getId());
			if(dataToWrite == null)
				newPage.setBlock((long) (Math.random() * 10000));
			else newPage.setBlock(Long.decode(dataToWrite));
			
			//dataToWrite = null;
		}

		if(stepByStep && step == -1) {
			this.updateLog("Checking the following...\nTLB, PT, Capacity");
		}

		boolean pageInTLB = false;
		Iterator <VirtualPage> itV = pages.iterator();

		while(itV.hasNext()) {
			VirtualPage p = itV.next();
			if(p.getId() == newPage.getId()) {
				pageInTLB = true;
				break;
			}
		}

		if((!stepByStep || (stepByStep && step == 0)) && (!pageInTLB)) {

			if(this.currCapacity + this.pageSize > this.memSize) {

				TLBRank = 0;
				memRank = -1;
				boolean canMove = true;
				PhysicalPage physPageToRemove;
				int diskIndex = 0;
				
				if(this.algorithm == PRA.FIFO) {
					this.updateLog("Not enough memory capacity!\n Using FIFO algorithm to free up space.");
					System.out.println("ON FIFO");
					diskIndex = 0;
				}
				
				if(this.algorithm == PRA.CLK) {
					
					this.updateLog("Not enough memory capacity!\n Using CLK algorithm to free up space.");
					boolean found = false;
					
					while(!found) {
						System.out.println("CLK HAND: " + clkHand);
						if(clkBuff[clkHand] == false) {
							clkBuff[clkHand] = true;
							
						
							
							if(clkHand == this.physicalMem.size() - 1)
								clkHand = 0;
							else clkHand ++;
						}else {
							found = true;
							diskIndex = clkHand;
						}
					}
				}
				
				if(this.algorithm == PRA.LRU) {
					
					this.updateLog("Not enough memory capacity!\n Using LRU algorithm to free up space.");
					
					LocalTime min = recents.get(0);
					
					Iterator <LocalTime> ltit = recents.iterator();
					
					while(ltit.hasNext()) {
						LocalTime curr = ltit.next();
						if(curr.compareTo(min) < 0) {
							min = curr;
						}
					}
					
					ltit = recents.iterator();
					
					while(ltit.hasNext()) {
						if(ltit.next().equals(min)) {
							break;
						}
						
						diskIndex ++;
					}
				}
				
				this.moveToDisk(diskIndex, stepByStep);
			}else {
				this.updateLog("Memory capacity \nis sufficient.");
			}
		}

		if(!stepByStep || (stepByStep && step == 0)) {
			newPage.setValid(true);
		}
		if(stepByStep && step == 0) {
			System.out.println("RETURN FROM STEP 0");
			return;
		}

		if(!stepByStep || (stepByStep && step == 1)) {			
			pageInTLB = false;
			itV = TLBEntries.iterator();

			while(itV.hasNext()) {
				VirtualPage p = itV.next();
				if(p.getId() == newPage.getId()) {
					pageInTLB = true;
					break;
				}
			}

			TLBSearches ++;

			if(!pageInTLB) {
				
				if(TLBEntries.size() >= TLBSize) {
					TLBEntries.remove(0);
					TLBRank = 0;
					
					this.clearTable(TLB);
					Iterator <VirtualPage> it = TLBEntries.iterator();
					while(it.hasNext()) {
						this.addToTable(it.next(), TLB);
					}
					
				}
				
				TLBEntries.add(newPage);
				if(stepByStep && step == 1) {
					this.updateLog("TLB Cache miss!\nPage 0x" + Long.toHexString(currID) + " has to be fetched.\nFetching...");
				}
				this.addToTable(newPage, TLB);
			}
			else {
				if(stepByStep && step == 1) {
					this.updateLog("TLB Cache hit!");

				}
				this.hitCount ++;
			}
		}

		if(stepByStep && step == 1) {
			System.out.println("RETURN FROM STEP 1");
			return;
		}

		if(!stepByStep || (stepByStep && step == 2)) {
			boolean pageInPT = false;
			Iterator <VirtualPage> it = pages.iterator();
			VirtualPage foundPage = null;

			while(it.hasNext()) {
				VirtualPage p = it.next();
				if(p.getId() == newPage.getId()) {
					pageInPT = true;
					foundPage = p;
					break;
				}
			}

			hitRate = ((float) hitCount / (float) TLBSearches) * 100;
			this.updateHitRate(hitRate);
			
			if(stepByStep && step == 2 && pageInPT)
				this.updateLog("Page " + newPage.getId() + " found in PT!");
			
			if(stepByStep && step == 2 && !pageInPT)
				this.updateLog("Page " + newPage.getId() + " not found in PT.\nFetching...");
		}
		
		if(stepByStep && step == 2) {
			System.out.println("RETURN FROM STEP 2");
			return;
		}
		
		long currBlock = -1;
			
			if(!stepByStep || (stepByStep && (step == 3))) {
				
				boolean pageInPT = false;
				Iterator <VirtualPage> it = pages.iterator();
				VirtualPage foundPage = null;

				while(it.hasNext()) {
					VirtualPage p = it.next();
					if(p.getId() == newPage.getId()) {
						pageInPT = true;
						foundPage = p;
						currBlock = p.getBlock();
						newPage.setBlock(p.getBlock());
						break;
					}
				}
				
				this.updateLog("Fetching page\ninto main memory...");
			
			if(pageInPT) {
				
				if(foundPage.getPhysicalPage() == -1) {

					int rmIndex = 0;
					Iterator <String> sit = disk.iterator();

					while(sit.hasNext()) {
						String curr = sit.next();

						if(Long.parseLong(curr) == foundPage.getLoc()) {
							break;
						}

						rmIndex ++;
					}

					disk.remove(rmIndex);

					this.clearTable(diskTable);
					sit = disk.iterator();

					if(!sit.hasNext()) {
						this.addToTable(" ", diskTable);
					}

					while(sit.hasNext()) {
						this.addToTable(sit.next(), diskTable);
					}


					if(stepByStep && step == 2)
						this.updateLog("Page " + newPage.getId() + " found in PT!\nIt is on disk\nLoading from disk...");

					boolean canMove;
					int swapIndex = 0;

					if(this.algorithm == PRA.FIFO) {
						swapIndex = 0;
					}
					
					if(this.algorithm == PRA.CLK) {
						
						boolean found = false;
						
						while(!found) {
							if(clkBuff[clkHand] == false) {
								clkBuff[clkHand] = true;
								
								if(clkHand == this.physicalMem.size() - 1)
									clkHand = 0;
								else clkHand ++;
							}else {
								found = true;
								swapIndex = clkHand;
							}
						}
					}
					
					if(this.algorithm == PRA.LRU) {
						
						LocalTime min = recents.get(0);
						
						Iterator <LocalTime> ltit = recents.iterator();
						
						while(ltit.hasNext()) {
							LocalTime curr = ltit.next();
							if(curr.compareTo(min) < 0) {
								min = curr;
							}
						}
						
						ltit = recents.iterator();
						
						while(ltit.hasNext()) {
							if(ltit.next().equals(min)) {
								break;
							}
							
							swapIndex ++;
						}
					}
					
					this.moveToDisk(swapIndex, stepByStep);
					

					foundPage.setPhysicalPage((long) (Math.random() * 10000));
					foundPage.setLoc(foundPage.getId());
					
					System.out.println("DATA TO WRITE : " + dataToWrite);
					System.out.println("CURRENT BLOCK: " + currBlock);
					
					if(dataToWrite == null)
						if(currBlock == -1)
							foundPage.setBlock((long) (Math.random() * 10000));
						else foundPage.setBlock(currBlock);
					else foundPage.setBlock(Long.decode(dataToWrite));
					
					dataToWrite = null;
					

					PhysicalPage newPhysPage = new PhysicalPage(++memRank, foundPage.getPhysicalPage(), foundPage.getBlock());
					newPhysPage.setVID(foundPage.getId());
					physicalMem.add(newPhysPage);
					
					recents.add(LocalTime.now());
					
					clkBuff[physicalMem.size() - 1] = false;
					
					
					TLBRank = 0;

					this.clearTable(TLB);
					this.clearTable(pageTable);
					this.clearTable(memTable);

					Iterator <VirtualPage> nit = TLBEntries.iterator();

					while(nit.hasNext()) {
						this.addToTable(nit.next(), TLB);
					}

					nit = pages.iterator();

					while(nit.hasNext()) {
						this.addToTable(nit.next(), pageTable);
					}

					Iterator <PhysicalPage> pit = physicalMem.iterator();

					while(pit.hasNext()) {
						this.addToTable(pit.next(), memTable);
					}

				}
				else {
					
					System.out.println("DATA TO WRITE : " + dataToWrite);
					
					if(dataToWrite == null)
						if(currBlock == -1)
							foundPage.setBlock((long) (Math.random() * 10000));
						else foundPage.setBlock(currBlock);
					else foundPage.setBlock(Long.decode(dataToWrite));
					dataToWrite = null;
					
					Iterator <PhysicalPage> pit2 = physicalMem.iterator();
					while(pit2.hasNext()) {
						
						PhysicalPage currP = pit2.next();
						if(currP.getVID() == foundPage.getId()) {
							
							currP.setBlock(foundPage.getBlock());
							System.out.println(currP.getVID() + " VS " + foundPage.getBlock());
						}
						
					}
					
					System.out.println("asdfasdfasdfasdf");
					
					TLBRank = 0;

					this.clearTable(TLB);
					this.clearTable(pageTable);
					this.clearTable(memTable);

					Iterator <VirtualPage> nit = TLBEntries.iterator();

					while(nit.hasNext()) {
						this.addToTable(nit.next(), TLB);
					}

					nit = pages.iterator();

					while(nit.hasNext()) {
						this.addToTable(nit.next(), pageTable);
					}

					Iterator <PhysicalPage> pit = physicalMem.iterator();

					while(pit.hasNext()) {
						this.addToTable(pit.next(), memTable);
					}

				}  
			}
			
			else {
				pages.add(newPage);
				PhysicalPage newPhysicalPage = new PhysicalPage(++memRank, newPage.getPhysicalPage(), newPage.getBlock());
				newPhysicalPage.setVID(newPage.getId());
				physicalMem.add(newPhysicalPage);
				
				recents.add(LocalTime.now());
				
				Iterator <LocalTime> ldit = recents.iterator();
				
				
				clkBuff[physicalMem.size() - 1] = false;

				if(!onDisk && !movedToDisk) {
					this.updateCapacity(this.pageSize);
				}
				
				this.clearTable(TLB);
				TLBRank = 0;

				Iterator <VirtualPage> nit = TLBEntries.iterator();

				while(nit.hasNext()) {
					this.addToTable(nit.next(), TLB);
				}

				this.addToTable(newPhysicalPage, this.memTable);

				this.addToTable(newPage, pageTable);
			}
		}
		
		if(stepByStep && step == 3) {
			System.out.println("RETURN FROM STEP 4");
			return;
		}
		
		if(stepByStep && step == 4) {
			this.skipLoad = true;
			this.loadStep = -1;
			this.movedToDisk = false;
			dataToWrite = null;
			this.updateLog("Loading is done!");
		}
		
		if(!stepByStep) {
		dataToWrite = null;
		}
	}

	public void updateHitRate(float hitRate) {
		this.hitField.setText(Float.toString(hitRate));
	}

	public void goStepByStep() {
		this.skipLoad = false;
	}

	public void clearTable(JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);
	}

	public void addToTable(Object o, JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		if(o.getClass() == String.class) {
			try {
			model.addRow(new Object[]{"0x" + Integer.toHexString(Integer.parseInt(o.toString()))});
			}catch(NumberFormatException nfex) {
				model.addRow(new Object[]{"0x" + o.toString()});
			}
		}
		else if(o.getClass() == VirtualPage.class) {
			if(table != TLB) {
				VirtualPage p = (VirtualPage) o;
				if(p.getPhysicalPage() != -1) {
					model.addRow(new Object[]{"0x" + Long.toHexString(p.getId()), this.VIDToAddress(p.getId())});
				}
				else {
					model.addRow(new Object[]{"0x" + Long.toHexString(p.getId()), "Disk"});
				}
			}
			else {
				VirtualPage p = (VirtualPage) o;
				if(p.getPhysicalPage() != -1) {
					TLBRank ++;
					model.addRow(new Object[]{TLBRank, "0x" + Long.toHexString(p.getId()), this.VIDToAddress(p.getId())});
				}else {
					TLBRank ++;
					model.addRow(new Object[]{TLBRank, "0x" + Long.toHexString(p.getId()), "Disk"});
				}
			}
		}
		else if(o.getClass() == PhysicalPage.class) {
			PhysicalPage p = (PhysicalPage) o;
			model.addRow(new Object[] {p.getIndex(), this.indexToHexAddr(p.getIndex()), Long.toHexString(p.getBlock())});
			//model.addRow(new Object[] {p.getIndex(), this.indexToHexAddr(p.getIndex()), p.getVID()});
		}
		else if(o.getClass() == Chunk.class) {
			Chunk a = (Chunk) o;
			model.addRow(new Object[] {a.getName(), a.getStart(), a.getEnd()});
		}
	}

	public void updateCapacity(long increase) {
		this.currCapacity += increase;
		this.capacityField.setText(Long.toString(this.currCapacity) + " / " + Long.toString(this.memSize));
		this.progressBar.setValue((int) this.currCapacity);
	}
	
	public String indexToHexAddr(long index) {
		String addr = "0x";
		Long addr2 = this.pageSize * index;
		return addr + Long.toHexString(addr2);
	}
	
	public String VIDToAddress(long VID) {
		String addr = null;
		Iterator <PhysicalPage> it = physicalMem.iterator();
		while(it.hasNext()) {
			PhysicalPage pp = it.next();
			if(pp.getVID() == VID) {
				addr = this.indexToHexAddr(pp.getIndex());
				return addr;
			}
		}
		
		return addr;
	}
	
	public void setWriteData(String data) {
		this.dataToWrite = data;
	}
}