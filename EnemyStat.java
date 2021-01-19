public class EnemyStat {
	public String name, region, atkType, pFile, eFile;
	public int chance, veinMin, veinMax, walkF, actionF, walkC, actionC, xp, hp, dmg, range, size, pSpd, pSize, pRange;
	public double spd, atkspd, dropMod;

	public EnemyStat(String line) {
		String[] stats = line.split(" ");
		name = stats[0];
		region = stats[1];
		chance = ti(stats[2]);
		veinMin = ti(stats[3]);
		veinMax = ti(stats[4]);
		walkF = ti(stats[5]);
		walkC = ti(stats[6]);
		actionF = ti(stats[7]);
		actionC = ti(stats[8]);
		atkType = stats[9];
		xp = ti(stats[10]);
		hp = ti(stats[11]);
		dmg = ti(stats[12]);
		spd = Double.parseDouble(stats[13]);
		range = ti(stats[14]);
		atkspd = Double.parseDouble(stats[15]);
		size = ti(stats[16]);
		pSpd = ti(stats[17]);
		pRange = ti(stats[18]);
		pSize = ti(stats[19]);
		pFile = stats[20];
		eFile = stats[21];
		dropMod = Double.parseDouble(stats[22]);


	}

	public String toString(){
		return name +" "+ region +" "+ chance +" "+ veinMin +" "+ veinMax +" "+ walkF +" "+ actionF +" "+ atkType +" "+ xp +" "+ hp +" "+ dmg +" "+ spd +" "+ range +" "+ size; 
	}

	private int ti (String s) {
		return Integer.parseInt(s);
	}
}