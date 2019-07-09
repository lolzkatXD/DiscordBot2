package DiscordBot2.src.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Draft extends Command {
    //
    private final EventWaiter waiter;
    //Object declaration
    private Random random; //for RNJESUS
    //Variable declaration
    private String cap1;
    private String cap2; //captains for each team
    private String ban; //champion to ban
    //Array declaration
    private String[] member; //members which will appear for team
    private String[] team1; //team blue
    private String[] team2; //team red
    private String[] banned; //banned champions
    //
    private CommandEvent event;

    public Draft(EventWaiter waiter) {
        this.waiter = waiter;
        this.name = "draft";
        this.help = "says hello and waits for a response";
        this.aliases = new String[]{"draft"};
        random = new Random();
        ban = " ";
        member = new String[8];
        team1 = new String[5];
        team2 = new String[5];
        banned = new String[4];
        banned[0] = " ";
        banned[1] = " ";
        banned[2] = " ";
        banned[3] = " ";
    }

    @Override
    protected void execute(CommandEvent event) {
        this.event = event;
        event.reply("Enter the captains");
        waiter.waitForEvent(MessageReceivedEvent.class,
                // make sure it's by the same user, and in the same channel
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> setCaptains(event, e.getMessage().getContentRaw()),
                // if the user takes more than a minute, time out
                1, TimeUnit.MINUTES, () -> event.reply("Sorry, you took too long."));

        event.reply("captbreak");

        try {
            event.wait((15 * 1000));
        } catch (Exception e) {
            event.reply(e.toString());
        }

        event.reply("breakbreak");

        waiter.waitForEvent(MessageReceivedEvent.class,
                // make sure it's by the same user, and in the same channel
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> setMembers(event, e.getMessage().getContentRaw()),
                // if the user takes more than a minute, time out
                1, TimeUnit.MINUTES, () -> event.reply("Sorry, you took too long."));

        event.reply("membreak");

        randomizeMember();
        splitMembers();

        event.reply("Ban phase " + 1);
        event.reply("Enter first ban " + cap1);
        waiter.waitForEvent(MessageReceivedEvent.class,
                e -> e.getAuthor().getAsTag().equals(cap1) && e.getChannel().equals(event.getChannel()),
                e -> event.reply("baned"),
                1, TimeUnit.MINUTES, () -> event.reply("Sorry, you took too long."));
        event.reply("Ban phase " + 2);


        event.reply("Ban phase " + 3);

        event.reply("Ban phase " + 4);


    }

    private void setCaptains(CommandEvent event, String message) {
        try {
            setCaptain(message.split(" "));
        }
        catch (ArrayIndexOutOfBoundsException e) {
            event.reply(e.toString());
        }
    }

    private void setMembers(CommandEvent event, String message) {
        setMember(message.split(" "));
    }

    //setting the IDs for captains/leaders and other members
    //setting captains
    private void setCaptain(String[] CAP) {
        cap1 = CAP[0];
        cap2 = CAP[1];
    }

    //setting members
    private void setMember(String... MEM) {
        member = MEM;
    }

    //
    //randomly choosing the team members
    //
    public void randomizeMember() { //randomizing the order
        int low = 0; //lowest number for randomization
        int high = 100; //highest number for randomization
        int rand;
        rand = (low + (int) random.nextDouble() * high); //BLESSRNJESUS
        String temp; //used for swapping
        //handling first and last member separately
        if (rand > (high / 2)) { //swaping 0th member with 1st
            temp = member[0];
            member[0] = member[1];
            member[1] = temp;
        } else { //swapping 7th member with 0th
            temp = member[0];
            member[0] = member[7];
            member[7] = temp;
        }
        for (int i = 1; i < 7; i++) {
            rand = (low + (int) random.nextDouble() * high); //getting fresh RNJESUS
            if (rand > (2 * high / 3)) { //66.66% //swaping ith member with next one (i+1)
                temp = member[i];
                member[i] = member[i + 1];
                member[i + 1] = temp;
            } else if (rand > (high / 3)) {  //33.33% //swaping ith member with the previous one(i-1)
                temp = member[i - 1];
                member[i - 1] = member[i];
                member[i] = temp;
            }
        }
    }

    //splitting into teams
    public void splitMembers() {//splitting the members in 2 teams with their respective captains
        ////setting team one with their captain
        for (int i = 0; i < 10; i++) {
            if (i == 0)
                team1[0] = cap1;
            else if (i < 5)
                team1[i] = member[i - 1];
            else if (i == 5)
                team2[0] = cap2;
            else team2[i - 5] = member[i - 2];
        }
    }

    //
    //BAN PHASES
    //
    public boolean ban(int i, String BAN) {
        ban = BAN.trim().toLowerCase(); //trims the answer and converts it to lower case for better comparison
        if (bannable(ban))
            banned[i] = ban;
        else return false;
        return true;
    }

    //check if the entered champion is bannable
    private boolean bannable(String ban) {
        for (int i = 0; (!banned[i].equals(" ")); i++) {
            if (ban.equals(banned[i]))
                return false;
        }
        return true;
    }
}
