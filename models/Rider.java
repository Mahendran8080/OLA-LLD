package models;
public class Rider
{
   public int id;
   public String name;
   public Rider(int id,String name)
    {
        this.id=id;
        this.name=name;

        
    }

    public String getName()
    {
        return this.name;
    }

    public int getid()
    {
        return this.id;
    }

    public void setName(String name)
    {
         this.name=name;
    }

    public void setId(int id)
    {
        this.id=id;
    }

}