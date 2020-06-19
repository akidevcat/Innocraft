package live.innocraft.doorsopenday;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Participant implements Runnable {

    public int taskId;
    private final Player player;
    private final DoorsOpenDay core;

    public boolean hasStarted = false;

    public boolean isRussian = false;

    public boolean isProfilePassed;
    public boolean isInterviewPassed;
    public boolean isPTPassed;
    public boolean isEnglishPassed;
    public boolean isContestPassed;

    public boolean isPurchasedBVI;
    public boolean isPurchasedEGE;
    public boolean isPurchasedPortfolio;
    public boolean isPurchasedScore;
    public boolean isPurchasedInstruction;
    public boolean isPurchasedBadge;
    public boolean isPurchasedMerch;

    public boolean isCuratorUsed;
    public boolean isAzatUsed;
    public boolean isFoodUsed;

    public int gameScore;
    public int examScore;
    public int innocoinsBalance;

    public int time;

    public final Score sc_time;
    public final Score sc_balance;
    private final Objective obj;
    private final Scoreboard board;

    public Participant(DoorsOpenDay core, Player p, String lang) {
        this.player = p;
        this.core = core;

        reset();

        isRussian = lang.equals("russian");

        board = Bukkit.getScoreboardManager().getNewScoreboard();
        obj = board.registerNewObjective("ServerName", "dummy", "Admission");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        sc_time = obj.getScore(ChatColor.GRAY + (isRussian ? "» Время" : "» Time left"));
        sc_time.setScore(time);

        sc_balance = obj.getScore(ChatColor.GRAY + (isRussian ? "» Иннокоины" : "» Innocoins"));
        sc_balance.setScore(innocoinsBalance);

        p.setScoreboard(board);
    }

    public void reset() {
        isPurchasedBVI = false;
        isPurchasedEGE = false;
        isPurchasedPortfolio = false;
        isPurchasedScore = false;
        isPurchasedInstruction = false;
        isPurchasedBadge = false;
        isPurchasedMerch = false;
        isCuratorUsed = false;
        isAzatUsed = false;
        isFoodUsed = false;
        time = 0;
        gameScore = 0;
        examScore = 0;
        innocoinsBalance = 1000;
        hasStarted = false;
        time = DoorsOpenDay.CONST_START_TIME;
        player.getInventory().clear();
    }

    public void start() {
        hasStarted = true;
    }

    public void destruct() {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Bukkit.getScheduler().cancelTask(taskId);
        hasStarted = false;
    }

    public void updatePurchased() {
        if (isPurchasedBVI)
            obj.getScore(ChatColor.GRAY + (isRussian ? "» БВИ" : "» BVI")).setScore(1);
        else
            board.resetScores(isRussian ? "» БВИ" : "» BVI");
        if (isPurchasedMerch)
            obj.getScore(ChatColor.GRAY + (isRussian ? "» Мерч" : "» Merch")).setScore(1);
        else
            board.resetScores(isRussian ? "» Мерч" : "» Merch");
        if (isPurchasedBadge)
            obj.getScore(ChatColor.GRAY + (isRussian ? "» Бейдж" : "» Badge")).setScore(1);
        else
            board.resetScores(isRussian ? "» Бейдж" : "» Badge");
        if (isPurchasedInstruction)
            obj.getScore(ChatColor.GRAY + (isRussian ? "» Инструкция" : "» Instruction")).setScore(1);
        else
            board.resetScores(isRussian ? "» Инструкция" : "» Instruction");
        if (isPurchasedScore)
            obj.getScore(ChatColor.GRAY + (isRussian ? "» Фулл Тесты" : "» Tests Full")).setScore(1);
        else
            board.resetScores(isRussian ? "» Фулл Тесты" : "» Tests Full");
        if (isPurchasedPortfolio)
            obj.getScore(ChatColor.GRAY + (isRussian ? "» Портфолио" : "» Portfolio")).setScore(1);
        else
            board.resetScores(isRussian ? "» Портфолио" : "» Portfolio");
        if (isPurchasedEGE)
            obj.getScore(ChatColor.GRAY + (isRussian ? "» ЕГЭ 255+" : "» EGE 255+")).setScore(1);
        else
            board.resetScores(isRussian ? "» ЕГЭ 255+" : "» EGE 255+");
    }

    @Override
    public void run() {
        if (hasStarted) {
            if (time <= 0) {
                player.sendTitle(isRussian ? "Время вышло" : "Time over", isRussian ? "Попробуйте заново" : "Try again", 20, 60, 20);
                player.getInventory().clear();
                player.teleport(new Location(player.getWorld(),73, 37, 142));
                hasStarted = false;
                core.removeParticipant(player);
                return;
            }

            time -= 1;

            updateScores();
            updatePurchased();
        }
    }

    public void updateScores() {
        sc_time.setScore(time);
        sc_balance.setScore(innocoinsBalance);
    }
}
