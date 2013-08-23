package nu.vill.myplants.model;

public class Plant {
	private int id = 0;
	private String name = "";
	private String desc = "";
	private int patch_id = 0;
	
	/**
	 * Constructor with all parameters known
	 * @param id
	 * @param name
	 * @param desc
	 */
	public Plant(int id, String name, String desc, int patch_id) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.patch_id = patch_id;
	}

	/**
	 * Constructor for new plant (id not known)
	 * @param name
	 * @param desc
	 */
	public Plant(String name, String desc, int patch_id) {
		this.name = name;
		this.desc = desc;
		this.patch_id = patch_id;
	}

	
	// Getters and setters
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getDesc() {return desc;}
	public void setDesc(String desc) {this.desc = desc;}
	public int getPatch() {return patch_id;}
	public void setPatch(int patch_id) {this.patch_id = patch_id;}	
	
}
