package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private Timer timer;// inorder to use schedule function
	private int speed; // the period of time between the actions
	private int duration;// number of ticks
	private int tick;//tick counter




	public TimeService(int speed , int duration) { //receive those parameters from the jason file.
		super("TimeService");
		timer = new Timer();
		this.speed =speed;
		this.duration =  duration;

		tick = 1 ;// begins from 1

	}

	@Override
	protected void initialize() {


		TimerTask task = new TimerTask() {
			@Override
			public void run() {//increment the tick and send broadcast
				if (tick > duration) {
					sendBroadcast(new TerminateBroadcast());
				}
				else {
					sendBroadcast(new TickBroadcast(tick));
					tick++;
				}
			}
		};

		timer.scheduleAtFixedRate(task, 0, speed);// take the task and execute the next task according to speed.

		//Terminate Broadcast Callback
		subscribeBroadcast(TerminateBroadcast.class,cb->{
			timer.cancel();
			timer.purge(); // TODO; check
			terminate();
		});

	}





}
