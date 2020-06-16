

import javax.sound.midi.*;

import java.io.File;
import java.io.IOException;


/***
 * 
 * @author īȾ����
 *
 * �����࣬����loadSound��������
 */
public class Sound {

	String file = new String(System.getProperty("user.dir")+"/GameMusic/nor.mid");
	Sequence seq;
	Sequencer midi;
	boolean sign;
	public void loadSound(){
		try {
			seq = MidiSystem.getSequence(new File(file));
			midi = MidiSystem.getSequencer();
			midi.open();
			midi.setSequence(seq);
			midi.start();
			midi.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			
		} catch (InvalidMidiDataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sign = true;
	}
	
	public Sound(){
		
	}
	

	
	public void mystop(){
		midi.stop();
		midi.close();
		sign=false;
	}
	
	boolean isplay(){
		return sign;
	}

	
}
