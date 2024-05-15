import java.awt.image.*;

public class Word {
    public BufferedImage[] frames;
    public int type;

    public Word(BufferedImage[] frames, int type) {
        this.frames = frames;
        this.type = type;
    }
}
