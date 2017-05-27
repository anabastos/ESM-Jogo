package com.forcaesm.sprites;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Angela on 23/05/17.
 */

public class Boneco extends Sprite {
    private Boolean corpoVisivel;
    Vector2 previousPosition;

    public Boneco(Texture texture) {
        super(texture);
        corpoVisivel = false;
//        corpoVisivel = new corpoVisivel(letrasUsadas);
        previousPosition = new Vector2(getX(), getY());
    }

    public boolean hasMoved(){
        if(previousPosition.x != getX() || previousPosition.y != getY()){
            previousPosition.x = getX();
            previousPosition.y = getY();
            return true;
        }
        return false;
    }

    public boolean minimoMembros( ) {
        if (corpoVisivel == true) {
            return true;
        } else {
            return false;
        }
    }
}
