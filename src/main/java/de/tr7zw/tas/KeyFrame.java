package de.tr7zw.tas;

public class KeyFrame {

    public boolean forwardKeyDown;
    public boolean backKeyDown;
    public boolean leftKeyDown;
    public boolean rightKeyDown;
    public boolean jump;
    public boolean sneak;
	public float pitch;
	public float yaw;
	public boolean leftClick;
	public boolean rightClick;
	public boolean sprint;
	
	public KeyFrame(boolean forwardKeyDown, boolean backKeyDown, boolean leftKeyDown, boolean rightKeyDown,
			boolean jump, boolean sneak, float pitch, float yaw, boolean leftClick, boolean rightClick, boolean sprint) {
		super();
		this.forwardKeyDown = forwardKeyDown;
		this.backKeyDown = backKeyDown;
		this.leftKeyDown = leftKeyDown;
		this.rightKeyDown = rightKeyDown;
		this.jump = jump;
		this.sneak = sneak;
		this.pitch = pitch;
		this.yaw = yaw;
		//if(this.pitch > 90)this.pitch = 90;
		//if(this.pitch < -90)this.pitch = -90;
		//if(this.yaw > 180)this.yaw = 180;
		//if(this.yaw < -180)this.yaw = -180;
		this.leftClick = leftClick;
		this.rightClick = rightClick;
		this.sprint = sprint;
	}
    
}
