package nu.vill.myplants.model;

import java.util.ArrayList;

public class Garden {
	private int id = 0;
	private String name = "";
	private String desc = "";
	private ArrayList<Patch> patches = null;

	
	/**
	 *  Constructor with all but patches known
	 * @param id
	 * @param name
	 * @param desc
	 */
	public Garden(int id, String name, String desc) {
		this.id = id;
		this.name = name;
		this.desc = desc;
	}

	/**
	 * Constructor for new garden (id and patches not known)
	 * @param name
	 */
	public Garden(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	//Getters and setters
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getDesc() {return desc;}
	public void setDesc(String desc) {this.desc = desc;}
	public ArrayList<Patch> getPatches() {return patches;}
	public void setPatches(ArrayList<Patch> patches) {this.patches = patches;}
}
