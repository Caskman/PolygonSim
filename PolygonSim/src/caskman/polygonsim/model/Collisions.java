package caskman.polygonsim.model;

public class Collisions {
	
	public static float detectCollision(Collidable a,Collidable b) {
		float velMag1 = Vector.mag(a.getVelocity());
		float velMag2 = Vector.mag(b.getVelocity());
		
		float sampleFreq1 = velMag1 / a.getLargestDim();
		float sampleFreq2 = velMag2 / b.getLargestDim();
		
		float timeInterval = (sampleFreq1 < sampleFreq2)?1F/sampleFreq2:1F/sampleFreq1;
		
		for (float percent = 0F; percent <= 1F; percent = (percent >= 1F)?(percent + timeInterval):(((percent += timeInterval) > 1F)?1F:percent)) {
			if (compare(a,b,percent)) {
				return percent;
			}
		}
		
		return -1F;
	}
	
	private static boolean compare(Collidable a,Collidable b,float percent) {
		a.setCollisionPosition(percent);
		b.setCollisionPosition(percent);
		
		Rectangle r1 = a.getCollisionAABB();
		Rectangle r2 = b.getCollisionAABB();
		
		return Rectangle.intersect(r1,r2);
	}
	
//	if (percent >= 1F) {
//		percent = percent + timeInterval;
//	} else if ((percent += timeInterval) > 1F) {
//		percent = 1F;
//	} else {
//		percent = percent;
//	}
//	
//	if (percent >= 1F) {
//		percent = percent + timeInterval;
//	} else percent = ((percent += timeInterval) > 1F)?1F:percent;
//	
//	percent = (percent >= 1F)?(percent + timeInterval):(((percent += timeInterval) > 1F)?1F:percent);
}
