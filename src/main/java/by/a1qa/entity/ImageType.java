package by.a1qa.entity;

public enum ImageType {

    PNG(".png"), IMAGEPNG("image/png");

    private String type;

    private ImageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }



}