package edu.scranton.gallaghert8.objects;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "menu_item", indices = {@Index("id")})
public class MenuItem {
    @PrimaryKey(autoGenerate = true)
    protected int id;
    protected String name;
    protected String type;
    protected String description;
    protected float unitPrice;

    public MenuItem(int id, String name, String type, String description, float unitPrice) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.unitPrice = unitPrice;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public String toString() {
        return "[" + id + " , " + type + " , " + name + " , " + description + " , " + unitPrice + "]";
    }
}