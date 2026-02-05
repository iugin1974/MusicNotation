package test;

import java.util.HashMap;
import java.util.Map;

import musicEvent.Modus;
import musicEvent.NamedNote;
import musicEvent.Note;
import notation.KeySignature;

public class TestKeySignature {

    public static void main(String[] args) {

    	KeySignature ks;
    	for (int i = 0; i <=7; i++) {
    		System.out.println(i + " diesis");
    	ks = new KeySignature(i, 1, Modus.MAJOR_SCALE);
    	for (int j = 0; j < 12; j++) {
    		int alt = ks.getAlteration(j);
    		String s = null;
    		if (alt == -1) s = "b";
    		if (alt == 0) s = "-";
    		if (alt == 1 ) s = "#";
    		if (alt == 2) s = "x";
    	//	System.out.print(s + " ");
    	}
    	System.out.println();
    	}
}

}
