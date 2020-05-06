import java.util.concurrent.CopyOnWriteArrayList; 
import java.util.Random;

public class MusicalChairs implements Runnable
{
	// Set up chairs
	CopyOnWriteArrayList<Chair> chairs = new CopyOnWriteArrayList<Chair>();
	int chairsAvailable = 0;
	int chairsOccupied = 0;

	// Game details
	final int TOTAL_PLAYERS;
	volatile int numPlayersReady = 0;
	int roundsRemaining;
	int currentRound = 1;
	volatile boolean roundEnded = false;
	Thread winner = null;

	public MusicalChairs(int totalPlayers)
	{
		TOTAL_PLAYERS = totalPlayers;
		roundsRemaining = TOTAL_PLAYERS - 1;
		chairsAvailable = TOTAL_PLAYERS;

		// Set up chairs
		for (int i = 0; i < TOTAL_PLAYERS; i++)
		{
			chairs.add(new Chair("C" + (i+1)));
		}
	}

	// Checks if current thread running is the emcee
	public boolean isEmcee()
	{
		if(Thread.currentThread().getName().equals("Emcee"))
			return true;
		else
			return false;
	}


	public void stopMusic()
	{
		synchronized (this) {
			notifyAll();
		}
	}

	public void playMusic()
	{
		for(int i = 0; i < chairs.size(); i++)
		{
			chairs.get(i).clearSeat();
		}

		chairsOccupied = 0;
	}

	@Override
	public void run()
	{

		// ########## EMCEE waits for players ###########
		if(isEmcee())
		{
         // Waits for players to be ready and in sync
			while(numPlayersReady < TOTAL_PLAYERS){	}

         // Announces beginning of game and round
			System.out.println("BEGIN " + TOTAL_PLAYERS + " players");
			System.out.println("\nround " + currentRound);
         numPlayersReady = 0;

			playMusic();
			RemoveOneChair();
			System.out.println("music off");
			stopMusic();
		}
		// #############################################
		// ############# Players ready up and wait ##############
		else
		{
			getPlayersReady();
		}
		// #############################################


		// ########## The game has begun ###############
		while (roundsRemaining > 0)
		{
			// Emcee algorithm for the round
			if(isEmcee())
			{
				// Wait for all players to find a chair
				while(!roundEnded) { }

				// Once done, set up variables for the next round
				roundEnded = false;
				roundsRemaining--;
				currentRound++;
				numPlayersReady = 0;

            // Once the round has reached the original total number of players, then it must be over
				if(currentRound == TOTAL_PLAYERS)
				{
					System.out.println("\n" + winner.getName() + " wins!");
					System.out.println("END");

               // One last stop to release the winner thread and end the whole program.
					stopMusic();
					return;
				}

				// Begin new round
				System.out.println("\nround " + currentRound);
				playMusic();
				RemoveOneChair();
				System.out.println("music off");
				stopMusic();
			}
			// Player algorithm for the round
			else
			{
				Chair chair = FindChair();

            // If this thread was able to secure a chair
				if(chair != null)
				{
               // Display its success and update program variables
					synchronized(this) {
						numPlayersReady++;
						System.out.println(Thread.currentThread().getName() + " sat in " + chair.getName());
						winner = Thread.currentThread();
					}
				}
            // This thread couldn't find a chair and lost
				else
				{
					// This thread already knows it lost, but it will spin and wait until the other threads
					// have announced that they found a seat
					while (numPlayersReady < (TOTAL_PLAYERS - currentRound)) { }

					// Announce the loser
					synchronized(this) {
						System.out.println(Thread.currentThread().getName() + " lost");
					}

					// Let the emcee know the round is over, and we have a loser
					roundEnded = true;

					// Kill the losing thread
					return;
				}

            // After threads have finished, wait for emcee to start next round
				try
				{
					synchronized(this)
					{
						wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Chair FindChair()
	{
      // Start with a random chair number
		Random rand = new Random();
		int randomChair = rand.nextInt(chairs.size());

      // Try to claim the random chair
		while(!chairs.get(randomChair).takeSeat())
		{
         // If all chairs have been claimed, then this thread lost
			if(chairsOccupied == chairsAvailable)
				return null;

         // If the randomChair is taken, then try the next chair in line
			randomChair =  (randomChair + 1) % chairs.size();
		}

      // This thread found a chair, so return it
		synchronized (this) { chairsOccupied++; }
		return chairs.get(randomChair);
	}

   // Remove last chair
	private void RemoveOneChair()
	{
		chairs.remove(chairs.size()-1);
		chairsAvailable--;
	}

	// Makes the players wait for the game to start
	private void getPlayersReady()
	{
		synchronized (this)
		{
			numPlayersReady++;

			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
