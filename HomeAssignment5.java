/**
 * @(#)HomeAssignment5.java
 * @author Lindsey Ferguson
 * @version 1.00 2022/2/27
 */

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.io.*; 
import java.util.*;
import java.util.ArrayList;
import java.awt.geom.Line2D;
import java.awt.GridLayout;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

public class HomeAssignment5 extends JFrame{
	private Graph directedGraph; 
    public HomeAssignment5() {
    	super("Directed Graph");
    	directedGraph = new Graph(); 
    	add(directedGraph);
    }
    
    public static void main(String[]args){
		HomeAssignment5 frame = new HomeAssignment5();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  	frame.setSize(550, 550); 
	  	frame.setVisible(true); 
	}
}

class Graph extends JPanel{
	private JPanel format1,format2,nodePanel,arcPanel,optionPanel;
	private JLabel nodeLabel,arcLabel,optionLabel;
	private JButton nodeDelete,nodeInsert,nodeMove,arcInsert,arcDelete,saveButton,openButton,clearButton,arcInsertLoop;

	private ArrayList<Line2D.Double> arcList,moveArcList,oldCoordArcList,delArcList; 
	private ArrayList<Ellipse2D> loopList,nodesList,delLoopList; 
	private ArrayList<Integer> endingArcList; 

	private JPopupMenu popUpMenuNode,popUpMenuArcs,popUpMenuLoops;
	private JLabel statusBar;
		
	private Ellipse2D dragged,selected,selected1,selected2;
	private Point offset,tmpPoint,firstNodePoint,secondNodePoint; 

	private boolean nodeInsertFlag,nodeMoveFlag,arcInsertFlag,arcInsertLoopFlag,arcDeleteFlag,nodeDeleteFlag,wait; 

	public Graph(){
		nodesList = new ArrayList<>();
		arcList = new ArrayList<>();
		loopList = new ArrayList<>();
		moveArcList = new ArrayList<>();
		endingArcList= new ArrayList<>();
		oldCoordArcList= new ArrayList<>();
		delArcList = new ArrayList<>();
		delLoopList = new ArrayList<>();
			
		selected1=selected2=null;
		wait=nodeInsertFlag=nodeMoveFlag=arcInsertFlag=arcDeleteFlag=arcInsertLoopFlag =nodeDeleteFlag=false;
		
		popUpMenuNode= new JPopupMenu("Node Options");
		popUpMenuArcs= new JPopupMenu("Arc Options");
		popUpMenuLoops= new JPopupMenu("Loop Options");

		JMenuItem deleteOp2 = new JMenuItem("delete");
		JMenuItem moveOp = new JMenuItem("move");
        JMenuItem deleteOp = new JMenuItem("delete");
        JMenuItem deleteOp3 = new JMenuItem("delete loop");
        popUpMenuNode.add(moveOp);
        popUpMenuNode.add(deleteOp);
        popUpMenuArcs.add(deleteOp2);
		popUpMenuLoops.add(deleteOp3);
		
		statusBar = new JLabel("Click On Any Option! ");
		this.add(statusBar,BorderLayout.NORTH);
		
		format1 = new JPanel();
		format1.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
		format2 = new JPanel(); 
		format2.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		nodePanel = new JPanel(new BorderLayout());
		arcPanel = new JPanel(new BorderLayout());
		optionPanel = new JPanel(new BorderLayout()); 
			
		nodeLabel = new JLabel("Node Options"); 
		arcLabel = new JLabel("Arc Options"); 
		optionLabel = new JLabel("Graph Options");
		
		ButtonHandler handler = new ButtonHandler();
		nodeDelete= new JButton("Delete");
		nodeDelete.addActionListener(handler);
		nodeInsert= new JButton("Insert");
		nodeInsert.addActionListener(handler);
		nodeMove= new JButton("Move");
		nodeMove.addActionListener(handler);
		arcInsert= new JButton("Insert b/w Nodes");
		arcInsert.addActionListener(handler);
		arcDelete= new JButton("Delete");
		arcDelete.addActionListener(handler);
		saveButton= new JButton("Save");
		saveButton.addActionListener(handler);
		openButton= new JButton("Open");
		openButton.addActionListener(handler);
		clearButton= new JButton("Clear");
		clearButton.addActionListener(handler);
		arcInsertLoop= new JButton("Insert Loop");
		arcInsertLoop.addActionListener(handler);
		
		nodePanel.add(nodeLabel, BorderLayout.NORTH);
		nodePanel.add(nodeDelete, BorderLayout.EAST);
		nodePanel.add(nodeMove, BorderLayout.WEST);
		nodePanel.add(nodeInsert, BorderLayout.SOUTH);
		arcPanel.add(arcLabel, BorderLayout.NORTH);
		
		arcPanel.add(arcDelete, BorderLayout.WEST);
		arcPanel.add(arcInsertLoop, BorderLayout.EAST);
		arcPanel.add(arcInsert, BorderLayout.SOUTH);
		
		format2.add(nodePanel, BorderLayout.WEST);
		format2.add(arcPanel, BorderLayout.WEST);
		
		optionPanel.add(optionLabel, BorderLayout.NORTH);
		optionPanel.add(saveButton, BorderLayout.EAST);
		optionPanel.add(openButton, BorderLayout.WEST);
		optionPanel.add(clearButton, BorderLayout.SOUTH);
		
		format1.add(format2, BorderLayout.WEST); 
		format1.add(optionPanel, BorderLayout.EAST); 
		this.add(format1,BorderLayout.NORTH);
		
		addMouseListener(
         	new MouseAdapter(){
         		@Override
         		public void mouseReleased(MouseEvent event){			
	 				if(nodeMoveFlag== true){
		 				if(dragged !=null){
		 					repaint();
		 				}
		 				dragged = null;
		 				offset=null;
		 				repaint();
	 				}
         		}
         		@Override
         		public void mouseClicked(MouseEvent event){	
         			if(event.getButton()==MouseEvent.BUTTON3){
         				for(int i = nodesList.size()-1;i>=0;--i){
         					if(nodesList.get(i).contains(event.getPoint())){
         						selected=nodesList.get(i);
         						popUpMenuNode.show(event.getComponent(),event.getX(),event.getY());
         						moveOp.addActionListener(new ActionListener() {
						            public void actionPerformed(ActionEvent e){
						              	nodeDeleteFlag=false;
										nodeInsertFlag = false; 
										nodeMoveFlag = true;
						            }
						        });
						        deleteOp.addActionListener(new ActionListener() {
						            public void actionPerformed(ActionEvent e){
						                int option = JOptionPane.showConfirmDialog(format1,"Do you wish to delete node?","Confirmation of Deletion",JOptionPane.YES_NO_OPTION);
			     						if (option == JOptionPane.YES_OPTION) {
											for(Line2D.Double arc:arcList){
												if(arc.intersects(selected.getX(),selected.getY(),15,15)){	 
													delArcList.add(arc);            		
												}
											}
											for(Ellipse2D loop:loopList){
												if(loop.intersects(selected.getX(),selected.getY(),20,20)){	 
													delLoopList.add(loop);            		
												}
	         								}
										    nodesList.remove(selected);
										}
										for(int i =0;i<arcList.size();i++){
				         					for(int m = 0;m<delArcList.size();m++){
				         						if(arcList.get(i)==delArcList.get(m)){
				         							arcList.remove(arcList.get(i));
				         						}
				         					}
				         				}
				         				for(int i =0;i<loopList.size();i++){
				         					for(int m = 0;m<delLoopList.size();m++){
				         						if(loopList.get(i)==delLoopList.get(m)){
				         							loopList.remove(loopList.get(i));
				         						}
				         					}
				         				}
						            }
						        });
         						break;
         					}
	         				for(Line2D.Double arc: arcList){		         					
	         					double dist2 = arc.ptLineDist(event.getPoint());
	         					if(dist2<=10){
	         						popUpMenuArcs.show(event.getComponent(),event.getX(),event.getY());
		         					deleteOp2.addActionListener(new ActionListener() {
							            public void actionPerformed(ActionEvent e){
			         						int option = JOptionPane.showConfirmDialog(format1,"Do you wish to delete arc?","Confirmation of Deletion",JOptionPane.YES_NO_OPTION);
			         						if (option == JOptionPane.YES_OPTION) {
											    arcList.remove(arc);	
											} 
											else{
											    arcDeleteFlag = false;
											}
										} 
							        });
     							break; 
     							}
     						break;
         					}
	        				for(Ellipse2D loop: loopList){
	         					if(Math.abs(loop.getY()-event.getPoint().y) <=50&&Math.abs(loop.getX()-event.getPoint().x) <=50){
	         						popUpMenuLoops.show(event.getComponent(),event.getX(),event.getY());
	         						deleteOp3.addActionListener(new ActionListener() {
							            public void actionPerformed(ActionEvent e){
			         						int option = JOptionPane.showConfirmDialog(format1,"Do you wish to delete loop?","Confirmation of Deletion",JOptionPane.YES_NO_OPTION);
			         						if (option == JOptionPane.YES_OPTION) {
											    loopList.remove(loop);
											} 
											else{
											    arcDeleteFlag = false;
											}
			         					}
			         				});
	         					}
	         					break;
	        				}
         				}
         			}
         			if(nodeInsertFlag == true&&event.getButton()==MouseEvent.BUTTON1){
	         			nodesList.add(new Ellipse2D.Float(event.getPoint().x-10,event.getPoint().y-10,20,20));
         			}
         			if(nodeDeleteFlag == true&&event.getButton()==MouseEvent.BUTTON1){
         				for(int i=0;i<nodesList.size();i++){
         					selected1=null;
         					if(nodesList.get(i).contains(event.getPoint())){
         						int option = JOptionPane.showConfirmDialog(format1,"Do you wish to delete node?","Confirmation of Deletion",JOptionPane.YES_NO_OPTION);
         						if (option == JOptionPane.YES_OPTION) {
         							for(Line2D.Double arc:arcList){
			         					if(arc.intersects(nodesList.get(i).getX(),nodesList.get(i).getY(),15,15)){	 
											delArcList.add(arc);            		
				                        }
		         					}
		         					for(Ellipse2D loop:loopList){
			         					if(loop.intersects(nodesList.get(i).getX(),nodesList.get(i).getY(),20,20)){	 
											delLoopList.add(loop);            		
				                        }
		         					}
								    nodesList.remove(nodesList.get(i));
								} 
								else{
								    nodeDeleteFlag = false;
								}
         					}         				
         				}
         				for(int i =0;i<arcList.size();i++){
         					for(int m = 0;m<delArcList.size();m++){
         						if(arcList.get(i)==delArcList.get(m)){
         							arcList.remove(arcList.get(i));
         						}
         					}
         				}
         				for(int i =0;i<loopList.size();i++){
         					for(int m = 0;m<delLoopList.size();m++){
         						if(loopList.get(i)==delLoopList.get(m)){
         							loopList.remove(loopList.get(i));         						
         						}
         					}
         				}
         			}
         		repaint();
         		}
         		@Override
         		public void mousePressed(MouseEvent event){
         			repaint();
         			for(Ellipse2D node: nodesList){
         				if(node.contains(event.getPoint())&&arcInsertFlag==false){
                            dragged = node;
                            offset = new Point(node.getBounds().x - event.getX(), node.getBounds().y - event.getY());
                            moveArcList.clear();
	         				oldCoordArcList.clear();
	         				endingArcList.clear();
                            break;
         				}
         			}
         			if(nodeMoveFlag == true){
                        for(Line2D.Double arc:arcList){
                        	if(dragged!=null){
                        		selected1=null;
	                        	if(arc.intersects(dragged.getX(),dragged.getY(),10,10)){
									moveArcList.add(arc);
	                        		oldCoordArcList.add(arc);	                        		
	                        	}
	                        	for(int i =0;i<moveArcList.size();i++){
	                    			if(Math.abs(moveArcList.get(i).getX1()-dragged.getX()) <=10&&Math.abs(moveArcList.get(i).getY1()-dragged.getY())<=10){
	                    				endingArcList.add(i,1);
	                    			}
	                    			else{
	                    				endingArcList.add(i,2);
	                    			}
		                        }
                        	}
                        }
         			}
	       			if(arcDeleteFlag == true){
         				for(Line2D.Double arc: arcList){
         					double dist=arc.ptLineDist(event.getPoint());
         					if(dist<=10){
         						int option = JOptionPane.showConfirmDialog(format1,"Do you wish to delete arc?","Confirmation of Deletion",JOptionPane.YES_NO_OPTION);
         						if (option == JOptionPane.YES_OPTION) {
								    arcList.remove(arc);
								    break;
								} 
								else{
								    arcDeleteFlag = false;
								}
         					}
         				}
         				for(Ellipse2D loop: loopList){
         					if(Math.abs(loop.getY()-event.getPoint().y) <=30&&Math.abs(loop.getX()-event.getPoint().x) <=30){
         						int option = JOptionPane.showConfirmDialog(format1,"Do you wish to delete loop?","Confirmation of Deletion",JOptionPane.YES_NO_OPTION);
         						if (option == JOptionPane.YES_OPTION) {
								    loopList.remove(loop);
								    break;
								} 
								else{
								    arcDeleteFlag = false;
								}
         					}
         				}
         			}
         			if(arcInsertFlag==true){
     					if(nodesList.size()>=2){
		         			if(wait==false){
								for(Ellipse2D node: nodesList){
			         				if(node.contains(event.getPoint())){
			                            selected1=node;
			                          	wait = true;
			         				}
								}
		         			}	
         					if(wait==true){
         						if(nodesList.size()>=2){
         							for(Ellipse2D node: nodesList){
										if(node.contains(event.getPoint())){
											selected2=node;
											if(selected1!=selected2){
												arcList.add(new Line2D.Double(selected1.getX()+10,selected1.getY()+10,selected2.getX()+10,selected2.getY()+10));
												wait=false;
												break;
											}
										}
									}	
         						}
							}
	         			}
         			}
         			if(arcInsertLoopFlag == true){
         				if(nodesList.size()>=1){	
							for(Ellipse2D node: nodesList){
		         				if(node.contains(event.getPoint())){
		                            selected1=node;
		                          	loopList.add(selected1);
		         				}
							}	
         				}
         			}		
         		}
         	}
         );	
		addMouseMotionListener(
         	new MouseAdapter(){
         		@Override
         		public void mouseDragged(MouseEvent event){
         			repaint();
	         		if(nodeMoveFlag==true){
	         			if(dragged !=null&& offset!=null){
	         				Point tmp = event.getPoint();
	         				tmp.x+=offset.x;
	         				tmp.y+=offset.y;
	         				Rectangle bounds = dragged.getBounds();
	                        bounds.setLocation(tmp);
	                        dragged.setFrame(bounds);
	         			}
	         			if(dragged!=null){
	         				for(int i=0;i<moveArcList.size();i++){
			         			if(endingArcList.get(i) == 1 ){
		                    		moveArcList.get(i).setLine(new Line2D.Double(dragged.getX()+10,dragged.getY()+10,oldCoordArcList.get(i).getX2(),oldCoordArcList.get(i).getY2()));
		                		}
		                		if(endingArcList.get(i) ==2){
		                			moveArcList.get(i).setLine(new Line2D.Double(oldCoordArcList.get(i).getX1(),oldCoordArcList.get(i).getY1(),dragged.getX()+10,dragged.getY()+10));
		                		}
	         				}
	         			}
	         		}        		
         		}
         	}
        );
	}
  	
	private class ButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event){
			if(event.getSource()== nodeDelete){
				statusBar.setText("Click on node to delete");		
				nodeDeleteFlag=true;
				nodeInsertFlag = false; 
				nodeMoveFlag = false;
				arcInsertFlag = false; 
				arcDeleteFlag = false; 
				arcInsertLoopFlag= false;
			}
			if(event.getSource()== nodeInsert){
				statusBar.setText("Click on canvas to insert node");
				nodeInsertFlag = true; 
				nodeMoveFlag = false; 
				nodeDeleteFlag=false;
				arcInsertFlag = false; 
				arcDeleteFlag = false; 
				arcInsertLoopFlag= false;
			}
			if(event.getSource()== nodeMove){
				statusBar.setText("Click on node to move");
				nodeMoveFlag = true;
				nodeInsertFlag = false; 
				nodeDeleteFlag=false;
				arcInsertFlag = false; 
				arcDeleteFlag = false; 
				arcInsertLoopFlag= false;
					
			}
			if(event.getSource()== arcInsert){
				statusBar.setText("Click on nodes to insert arc");
				arcInsertFlag=true; 
				nodeMoveFlag = false;
				nodeInsertFlag = false; 
				nodeDeleteFlag=false;
				arcInsertLoopFlag= false;
				arcDeleteFlag = false; 
				
			}
			if(event.getSource()== arcInsertLoop){
				statusBar.setText("Click on node to insert loop arc");
				arcInsertFlag=false; 
				nodeMoveFlag = false;
				nodeInsertFlag = false; 
				nodeDeleteFlag=false;
				arcInsertLoopFlag= true;
				arcDeleteFlag = false; 
			}
			
			if(event.getSource()== arcDelete){
				statusBar.setText("Click on arc to delete");
				arcDeleteFlag = true; 
				arcInsertFlag = false;
				arcInsertLoopFlag= false;
				nodeMoveFlag = false;
				nodeInsertFlag = false; 
				nodeDeleteFlag=false;
			}
			if(event.getSource()== saveButton){
				statusBar.setText("Save to txt file, to edit later");
				String saveFileName = JOptionPane.showInputDialog(format1,"Save As: (dont put .txt)");	
				try{
		  			PrintWriter fileWriter = new PrintWriter(new BufferedWriter (new FileWriter(saveFileName+".txt")));
			  		fileWriter.println("nodes");
					for(Ellipse2D node: nodesList){
						fileWriter.println(node.getX()+", "+node.getY());
					}
					fileWriter.println("arcs");
					for(Line2D.Double arc: arcList){
						fileWriter.println(arc.getX1()+", "+arc.getY1()+", "+arc.getX2()+", "+arc.getY2());
					}	
					fileWriter.println("loops");
					for(Ellipse2D loop: loopList){
						fileWriter.println(loop.getX()+", "+loop.getY());
					}				
					fileWriter.close(); 
			  	}
			  	catch (IOException e){
					System.out.println(e);
			  	}
			}
			if(event.getSource()== openButton){
				statusBar.setText("Open a txt file to load old graph");
				String openFileName = JOptionPane.showInputDialog(format1,"Enter file name: (dont put .txt) ");
				nodesList.clear();
				arcList.clear();
				repaint();
				try{
		  			Scanner openFileReader = new Scanner(new BufferedReader(new FileReader(openFileName+".txt")));
		  			statusBar.setText("file: "+openFileName+".txt is being opened");
		  			String tmp= "";
	  				while(openFileReader.hasNext()){
	  					tmp= openFileReader.nextLine();
	  					if(tmp.contains(",")){
	  						String[] coords = tmp.split(", ");
		  					nodesList.add(new Ellipse2D.Float(Float.parseFloat((String)coords[0]),Float.parseFloat((String)coords[1]),20,20)); 
	  					}
	  					if(tmp.equals("arcs")||tmp.equals("loops")){
	  						break; 
	  					}
		  			}
		  			while(openFileReader.hasNext()){
	  					tmp= openFileReader.nextLine();	
	  					if(tmp.contains(",")){
	  						String[] coords = tmp.split(", ",4);
		  					arcList.add(new Line2D.Double(Double.parseDouble((String)coords[0]),Double.parseDouble((String)coords[1]),Double.parseDouble((String)coords[2]),Double.parseDouble((String)coords[3])));
	  					}
	  					if(tmp.equals("loops")){
	  						break; 
	  					}
		  			}
		  			while(openFileReader.hasNext()){
		  				tmp= openFileReader.nextLine();	
		  				if(tmp.contains(",")){
	  						String[] coords = tmp.split(", ");
		  					loopList.add(new Ellipse2D.Float(Float.parseFloat((String)coords[0]),Float.parseFloat((String)coords[1]),20,20)); 
		  				}
			  		}
			  		repaint();
				}
			  	catch (IOException e){
					System.out.println(e);
	    		}
			}
			
			if(event.getSource()== clearButton){
				statusBar.setText("To clear graph select yes");
				int option = JOptionPane.showConfirmDialog(format1,"Do you wish to clear graph?","Confirmation",JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
				   	nodesList.clear();
				   	loopList.clear();
					arcList.clear();
					repaint();
				} 
			}
		}
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		
		for(Ellipse2D loop:loopList){//loops drawn here
			g.drawArc((int)loop.getX()-3,(int)loop.getY()-25,10,40,0,270);
			addLoopTriangle(g2d,loop);
		}
		
		for (Ellipse2D node : nodesList) {//actual nodes are drawn here
            if(dragged!=null){
	            if(node == dragged||node==selected1) {
	                g2d.setColor(new Color(8,247,254));//neon cyan
	                g2d.fill(new Ellipse2D.Double(node.getX()-2,node.getY()-2,24,24));
	            }
            }
            if(node == selected1&&arcInsertFlag==true||arcInsertLoopFlag==true&&node == selected1) {
                g2d.setColor(new Color(254,83,187));//neon pink 
                g2d.fill(new Ellipse2D.Double(node.getX()-2,node.getY()-2,24,24));
            }
            if(node == selected2&&arcInsertFlag==true) {
                g2d.setColor(new Color(245,211,0));//bright yellow
                g2d.fill(new Ellipse2D.Double(node.getX()-2,node.getY()-2,24,24));
            }
            g2d.setColor(Color.BLACK);
            g2d.fill(node);
        }
        
        for(Line2D.Double arc: arcList){//arcs drawn here
			g2d.draw(arc);			
			addTriangle(g2d,arc);	
		}
		repaint();
	}
	AffineTransform transform = new AffineTransform();
	private void addTriangle(Graphics2D g2d,Line2D.Double arc) {  
		Polygon triangle = new Polygon(new int[] {0,-6,6}, new int[] {-9,-21,-21}, 3);  
		transform.setToIdentity();
		double angle = Math.atan2(arc.getY2()-arc.getY1(),arc.getX2()-arc.getX1());
	    transform.translate(arc.getX2(), arc.getY2());
	    transform.rotate((angle-Math.PI/2d));  
	    Graphics2D g = (Graphics2D) g2d.create();
	    g.setColor(Color.MAGENTA);
	    g.setTransform(transform);   
	    g.fill(triangle);
	}
	
	public void addLoopTriangle(Graphics2D g2d,Ellipse2D loop){
    	Polygon triangle = new Polygon(new int[] {(int)loop.getX()+8,(int)loop.getX()-5+8,(int)loop.getX()+5+8}, new int[] {(int)loop.getY(),(int)loop.getY()-5,(int)loop.getY()-5}, 3);
    	Graphics2D g = (Graphics2D) g2d.create();
	    g.setColor(Color.MAGENTA);
	    g.fill(triangle);	
	}
}
