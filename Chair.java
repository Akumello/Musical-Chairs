public class Chair
{
	private String name;
	private Thread occupant;
	private boolean occupied;

	public Chair(String name)
	{
		this.name = name;
		occupied = false;
	}

	public String getName()
	{
		return name;
	}

	public Thread getOccupant()
	{
		if(occupied)
			return occupant;
		else
			return null;
	}

	public boolean isOccupied()
	{
		return occupied;
	}
	
	synchronized public boolean takeSeat()
	{
		if(!occupied)
		{
			occupant = Thread.currentThread();
			occupied = true;
			return true;
		}
		else
			return false;
	}

	public void clearSeat()
	{
		occupant = null;
		occupied = false;
	}
}
