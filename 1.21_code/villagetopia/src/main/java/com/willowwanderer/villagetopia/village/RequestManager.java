package com.willowwanderer.villagetopia.village;

public enum RequestSource {
    USER,
    HUNGRYVILLAGER,
    VILLAGER,
    HAPPYVILLAGER,
    TRADER
};

public class RequestManager {
    private Profession [] professions;
    public int [] workers;
    public Request [] requests;

    // Constructor
    
    // get list of professions
    private void getProfessionList(String configfile)
    {

    }

    // return highest priority profession as a balance
    public Profession getProfession(Skills skillList)
    {
        // 

        // check requests list 

        // Adds to workers list
    }


    
    // return highest priority (not taken) job
    public Job getJob(Profession profession, int skillLevel)
    {

    }

    // updates the job status of that particular job
    public void updateJobStatus(JobStatus status)
    {

    }

    // adds a request
    public void requestItems(Item [] items, RequestSource source)
    {
        // set request source as source (user requested items will end up in trade chest)

        // find the item/s recipe/s

        // if recipe/s not found then make traders request

        // if recipe/s found

        // check environment requirements for recipe/s

        // if environmental req is not met then set status to waiting with message waiting for X

        // find associated profession/s

        // if can't find profession/s, then set to trader
        
        // calculate recipe/s qty

        // check inventory for ingredients

        // if ingredients found then register new request/s as pending

        // if ingredients/s not found register new request/s as waiting

        // ---- request any missing items (self call)


    }

    // get's called by inventory manager to when items are placed into the community chests (i.e. user has added things for villagers)
    public void checkPendingJobsSatisfied(Item [] items)
    {
        // check if any pending jobs are for these items

        // reduce qty required 

        // or remove job
    }


    // adds routine requests i.e. for food, guards
    public void tick()
    {

    }
}
