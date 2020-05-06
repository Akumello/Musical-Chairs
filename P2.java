public class P2
{
	final static int DEFAULT_PLAYERS = 10;

	public static void main(String[] args)
	{
		int totalPlayers;
		if(args.length == 0)
			totalPlayers = DEFAULT_PLAYERS;
		else
			totalPlayers = Integer.valueOf(args[0]);


		Thread players[] = new Thread[totalPlayers];
		MusicalChairs game = new MusicalChairs(totalPlayers);
		Thread emcee = new Thread(game);
		emcee.setName("Emcee");
		emcee.start();

		for(int i = 0; i < totalPlayers; i++)
		{
			players[i] = new Thread(game);
			players[i].setName("P" + (i+1));
			players[i].start();
		}
	}

}
