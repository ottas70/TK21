package cz.cvut.fel.tk21.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Embeddable
public class ImageDetail {

    @Column
    private String originalName;
    @Column
    private int widthOriginal;
    @Column
    private int heightOriginal;

    @Column
    private String miniName;
    @Column
    private int widthMini;
    @Column
    private int heightMini;

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public int getWidthOriginal() {
        return widthOriginal;
    }

    public void setWidthOriginal(int widthOriginal) {
        this.widthOriginal = widthOriginal;
    }

    public int getHeightOriginal() {
        return heightOriginal;
    }

    public void setHeightOriginal(int heightOriginal) {
        this.heightOriginal = heightOriginal;
    }

    public String getMiniName() {
        return miniName;
    }

    public void setMiniName(String miniName) {
        this.miniName = miniName;
    }

    public int getWidthMini() {
        return widthMini;
    }

    public void setWidthMini(int widthMini) {
        this.widthMini = widthMini;
    }

    public int getHeightMini() {
        return heightMini;
    }

    public void setHeightMini(int heightMini) {
        this.heightMini = heightMini;
    }
}
