package live.innocraft.doorsopenday;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class DODCommands implements CommandExecutor {

    private final DoorsOpenDay core;

    public DODCommands(DoorsOpenDay core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equals("dod"))
            return false;
        if (args.length < 1)
            return false;
        switch (args[0]) {
            case "usefood":
                if (!sender.hasPermission("innocraft.dod.edit"))
                    return false;
                if (args.length != 2)
                    return false;
                Player p10 = Bukkit.getPlayer(args[1]);
                if (p10 == null)
                    return false;
                Participant pt10 = core.participants.get(p10);
                if (pt10 == null) {
                    p10.sendTitle(ChatColor.RED + "You're not in game.", "", 20, 40, 20);
                    return true;
                }
                if (!pt10.isPurchasedBadge) {
                    p10.sendTitle(pt10.isRussian ? ChatColor.RED + "Нет бейджика" : ChatColor.RED + "No badge", "", 20, 40, 20);
                    return true;
                }
                if (!pt10.isFoodUsed) {
                    if (pt10.isRussian)
                        p10.sendTitle(ChatColor.GREEN + "Wrap +Food", "You've received extra time: " + DoorsOpenDay.CONST_FOOD_BONUS, 20, 40, 20);
                    else
                        p10.sendTitle(ChatColor.GREEN + "Съеден Врап", "Вы получили доп. время: " + DoorsOpenDay.CONST_FOOD_BONUS, 20, 40, 20);
                    pt10.time += DoorsOpenDay.CONST_FOOD_BONUS;
                    pt10.isFoodUsed = true;
                }
                return true;
            case "useazat":
                if (!sender.hasPermission("innocraft.dod.edit"))
                    return false;
                if (args.length != 2)
                    return false;
                Player p9 = Bukkit.getPlayer(args[1]);
                if (p9 == null)
                    return false;
                Participant pt9 = core.participants.get(p9);
                if (pt9 == null) {
                    p9.sendTitle(ChatColor.RED + "You're not in game.", "", 20, 40, 20);
                    return true;
                }
                if (!pt9.isAzatUsed) {
                    if (pt9.isRussian)
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu azat-ru.yml " + args[1]);
                    else
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu azat-en.yml " + args[1]);
                    pt9.isAzatUsed = true;
                }
                return true;
            case "givecuratoritem":
                if (!sender.hasPermission("innocraft.dod.edit"))
                    return false;
                if (args.length != 2)
                    return false;
                Player p8 = Bukkit.getPlayer(args[1]);
                if (p8 == null)
                    return false;
                Participant pt8 = core.participants.get(p8);
                if (pt8 == null) {
                    p8.sendTitle(ChatColor.RED + "You're not in game.", "", 20, 40, 20);
                    return true;
                }
                if (pt8.isCuratorUsed)
                    return true;
                if (!pt8.isPurchasedMerch) {
                    pt8.isPurchasedMerch = true;
                    p8.sendTitle(ChatColor.GREEN + (pt8.isRussian ? "Держи" : "Take this"), pt8.isRussian ? "Вы получили Мерч" : "You've received Merch", 20, 40, 20);
                }
                else if (!pt8.isPurchasedBadge) {
                    pt8.isPurchasedBadge = true;
                    p8.sendTitle(ChatColor.GREEN + (pt8.isRussian ? "Держи" : "Take this"), pt8.isRussian ? "Вы получили Бейджик" : "You've received Badge", 20, 40, 20);
                }
                else if (!pt8.isPurchasedInstruction) {
                    pt8.isPurchasedInstruction = true;
                    p8.sendTitle(ChatColor.GREEN + (pt8.isRussian ? "Держи" : "Take this"), pt8.isRussian ? "Вы получили Инструкцию" : "You've received Instruction", 20, 40, 20);
                }
                pt8.isCuratorUsed = true;
                pt8.updatePurchased();
                return true;
            case "addscore":
                if (!sender.hasPermission("innocraft.dod.edit"))
                    return false;
                if (args.length != 2)
                    return false;
                Player p7 = Bukkit.getPlayer(args[1]);
                if (p7 == null)
                    return false;
                Participant pt7 = core.participants.get(p7);
                if (pt7 == null) {
                    p7.sendTitle(ChatColor.RED + "You're not in game.", "", 20, 40, 20);
                    return true;
                }
                pt7.examScore++;
                return true;
            case "pass":
                if (!sender.hasPermission("innocraft.dod.edit"))
                    return false;
                if (args.length != 3)
                    return false;
                Player p6 = Bukkit.getPlayer(args[1]);
                if (p6 == null)
                    return false;
                Participant pt6 = core.participants.get(p6);
                if (pt6 == null) {
                    p6.sendTitle(ChatColor.RED + "You're not in game.", "", 20, 40, 20);
                    return true;
                }
                switch (args[2]) {
                    case "english":
                        if (pt6.isEnglishPassed) {
                            p6.sendTitle(pt6.isRussian ? ChatColor.GREEN + "Пройден" : ChatColor.GREEN + "Passed", pt6.isRussian ? "Данный тест уже пройден" : "This test was passed", 20, 40, 20);
                            return true;
                        }
                        if (pt6.isRussian)
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu englishtest_a_ru.yml " + args[1]);
                        else
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu englishtest_a_en.yml " + args[1]);
                        pt6.isEnglishPassed = true;
                        break;
                    case "pt":
                        if (pt6.isPTPassed) {
                            p6.sendTitle(pt6.isRussian ? ChatColor.GREEN + "Пройден" : ChatColor.GREEN + "Passed", pt6.isRussian ? "Данный тест уже пройден" : "This test was passed", 20, 40, 20);
                            return true;
                        }
                        if (pt6.isRussian)
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu pttest_a_ru.yml " + args[1]);
                        else
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu pttest_a_en.yml " + args[1]);
                        pt6.isPTPassed = true;
                        break;
                    case "profile":
                        if (pt6.isProfilePassed) {
                            p6.sendTitle(pt6.isRussian ? ChatColor.GREEN + "Пройден" : ChatColor.GREEN + "Passed", pt6.isRussian ? "Данный тест уже пройден" : "This test was passed", 20, 40, 20);
                            return true;
                        }
                        if (pt6.isRussian)
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu profiletest_a_ru.yml " + args[1]);
                        else
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu profiletest_a_en.yml " + args[1]);
                        pt6.isProfilePassed = true;
                        break;
                    case "contest":
                        if (pt6.isContestPassed) {
                            p6.sendTitle(pt6.isRussian ? ChatColor.GREEN + "Пройден" : ChatColor.GREEN + "Passed", pt6.isRussian ? "Данный тест уже пройден" : "This test was passed", 20, 40, 20);
                            return true;
                        }
                        if (pt6.isRussian)
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu contesttest_a_ru.yml " + args[1]);
                        else
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu contesttest_a_en.yml " + args[1]);
                        pt6.isContestPassed = true;
                        break;
                    case "interview":
                        if (pt6.isInterviewPassed) {
                            p6.sendTitle(pt6.isRussian ? ChatColor.GREEN + "Пройден" : ChatColor.GREEN + "Passed", pt6.isRussian ? "Данный тест уже пройден" : "This test was passed", 20, 40, 20);
                            return true;
                        }
                        if (pt6.isRussian)
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu interviewtest_a_ru.yml " + args[1]);
                        else
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu interviewtest_a_en.yml " + args[1]);
                        pt6.isInterviewPassed = true;
                        break;
                }
                return true;
            case "finish":
                if (!sender.hasPermission("innocraft.dod.edit"))
                    return false;
                if (args.length != 2)
                    return false;
                Player p5 = Bukkit.getPlayer(args[1]);
                if (p5 == null)
                    return false;
                Participant pt2 = core.participants.get(p5);
                if (pt2 == null) {
                    p5.sendTitle(ChatColor.RED + "You're not in game.", "", 20, 40, 20);
                    return true;
                }
                if (pt2.examScore >= DoorsOpenDay.CONST_PASS_SCORE) {
                    p5.sendTitle(pt2.isRussian ? ChatColor.GREEN + "Полный Грант" : ChatColor.GREEN +  "Full Scholarship", (pt2.isRussian ? "Поздравляем! " : "Congratulations! ") + pt2.examScore + "/15", 20, 40, 20);
                } else {
                    p5.sendTitle(pt2.isRussian ? ChatColor.RED + "Список Ожидания" : ChatColor.RED + "Wait list", (pt2.isRussian ? "Попробуйте заново " : "Try Again ") + pt2.examScore + "/15", 20, 40, 20);
                }
                core.removeParticipant(p5);
                return true;
            case "leave":
                if (!(sender instanceof Player))
                    return false;
                core.removeParticipant((Player)sender);
                ((Player)sender).sendTitle(ChatColor.RED + "You've exited the game.", "", 20, 40, 20);
                return true;
            case "joinfree":
                if (!sender.hasPermission("innocraft.dod.edit"))
                    return false;
                if (args.length != 2)
                    return false;
                Player p4 = Bukkit.getPlayer(args[1]);
                if (p4 == null)
                    return false;
                core.removeParticipant(p4);
                p4.setAllowFlight(true);
                p4.setFlying(true);
                p4.sendTitle(ChatColor.BLUE + "Exploration Mode", "Press double space to fly", 20, 60, 20);
                return true;
            case "buymenu":
                if (!(sender instanceof Player))
                    return false;
                if (!core.participants.containsKey((Player)sender))
                    return false;
                if (core.participants.get((Player)sender).isRussian) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu buymenu-ru.yml " + ((Player) sender).getName());
                } else {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu buymenu-en.yml " + ((Player) sender).getName());
                }
                return true;
            case "menu":
                if (!sender.hasPermission("innocraft.dod.edit"))
                    return false;
                if (args.length != 2)
                    return false;
                Player ppp = Bukkit.getPlayer(args[1]);
                if (ppp == null)
                    return false;
                if (core.participants.containsKey(ppp)) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu gamemenu.yml " + args[1]);
                } else {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "openmenu startmenu.yml " + args[1]);
                }
                return true;
            case "join":
                if (!(sender instanceof Player))
                    return false;
                if (args.length != 2)
                    return false;
                if (core.participants.containsKey((Player)sender)) {
                    sender.sendMessage(core.participants.get((Player)sender).isRussian ? "Вы уже присоединились!" : "You're already in game!");
                    return true;
                }

                Participant pttt = core.addParticipant((Player)sender, args[1]);

                if (pttt.isRussian)
                    ((Player)sender).sendTitle(ChatColor.BLUE + "Удачи!", "Первым делом тебе нужно потрататить 1000 иннокоинов", 20, 100, 20);
                else
                    ((Player)sender).sendTitle(ChatColor.BLUE + "Good luck!", "Firstly you should spend 1000 innocoins", 20, 100, 20);
                if (((Player)sender).getGameMode() != GameMode.CREATIVE) {
                    ((Player) sender).setAllowFlight(false);
                    ((Player) sender).setFlying(false);
                }
                return true;
            case "tpcheck":
                if (!sender.hasPermission("innocraft.dod.edit"))
                    return false;
                if (args.length != 5)
                    return false;
                Player pp = Bukkit.getPlayer(args[1]);
                if (pp == null)
                    return false;
                int xx = Integer.parseInt(args[2]);
                int yy = Integer.parseInt(args[3]);
                int zz = Integer.parseInt(args[4]);
                Participant pt = core.participants.get(pp);
                if (pt == null) {
                    pp.teleport(new Location(pp.getWorld(), xx, yy, zz));
                    return true;
                }
                if (pt.isPurchasedBVI || (pt.isPurchasedEGE && pt.isPurchasedScore && pt.isPurchasedPortfolio)) {
                    pp.teleport(new Location(pp.getWorld(), xx, yy, zz));
                    pp.sendMessage(core.participants.get(pp).isRussian ? ChatColor.BLUE + "Охранник -> Вы: " + ChatColor.WHITE + "Вижу в списке, проходи!" : ChatColor.BLUE + "Guard > You: " + ChatColor.WHITE + "I see you in the list, come through");
                    pp.sendTitle(core.participants.get(pp).isRussian ? ChatColor.BLUE + "Время пошло" : ChatColor.BLUE + "Time started", "", 20, 40, 20);
                    pt.start();
                } else {
                    pp.sendMessage(core.participants.get(pp).isRussian ? ChatColor.BLUE + "Охранник -> Вы: " + ChatColor.WHITE + "Не вижу тебя в списке." : ChatColor.BLUE + "Guard > You: " + ChatColor.WHITE + "I can't find you in the list.");
                    pp.sendMessage(core.participants.get(pp).isRussian ? ChatColor.YELLOW + "Подсказка: " + ChatColor.WHITE + "Попробуйте другой набор!" : ChatColor.YELLOW + "Hint: " + ChatColor.WHITE + "Try using other starting kit!");
                    pp.sendMessage(core.participants.get(pp).isRussian ? ChatColor.YELLOW + "Подсказка: " + ChatColor.WHITE + "У вас должен быть БВИ, либо ЕГЭ+Тесты+Портфолио" : ChatColor.YELLOW + "Hint: " + ChatColor.WHITE + "You should have BVI, or EGE+Tests+Portfolio");
                    pp.sendMessage(core.participants.get(pp).isRussian ? ChatColor.GREEN + "Система: " + ChatColor.WHITE + "Ваш баланс был обнулен!" : ChatColor.GREEN + "System: " + ChatColor.WHITE + "Your balance was reset!");
                    pt.reset();
                    pt.updatePurchased();
                    pt.updateScores();
                }
                return true;
            case "tpvolunteer":
                if (!sender.hasPermission("innocraft.dod.edit"))
                    return false;
                if (args.length != 5)
                    return false;
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null)
                    return false;
                int x = Integer.parseInt(args[2]);
                int y = Integer.parseInt(args[3]);
                int z = Integer.parseInt(args[4]);
                if (!core.participants.containsKey(p)) {
                    p.teleport(new Location(p.getWorld(), x, y, z));
                    return true;
                }
                if (core.participants.get(p).isPurchasedMerch) {
                    p.teleport(new Location(p.getWorld(), x, y, z));
                    p.sendMessage(core.participants.get(p).isRussian ? ChatColor.BLUE + "Волонтер -> Вы: " + ChatColor.WHITE + "Прикольная футболка" : ChatColor.BLUE + "Volunteer -> You:" + ChatColor.WHITE + " Cool T-Shirt");
                } else {
                    p.teleport(new Location(p.getWorld(), x, y, z));
                    if (Math.random() <= DoorsOpenDay.CONST_MERCH_FINE_CHANCE) {
                        p.sendMessage(core.participants.get(p).isRussian ? ChatColor.BLUE + "Волонтер -> Вы: " + ChatColor.WHITE + "Ты кто такой?" : ChatColor.BLUE + "Volunteer -> You:" + ChatColor.WHITE + " Who are you?");
                        if (core.participants.get(p).isRussian)
                            p.sendTitle(ChatColor.RED + "Остановлен", "Вы потеряли время: " + DoorsOpenDay.CONST_MERCH_FINE, 20, 40, 20);
                        else
                            p.sendTitle(ChatColor.RED + "Suspended", "You've lost the time: " + DoorsOpenDay.CONST_MERCH_FINE, 20, 40, 20);
                        core.participants.get(p).time -= DoorsOpenDay.CONST_MERCH_FINE;
                    }
                }
                return true;
            case "buy":
                if (args.length != 2)
                    return false;
                if (!(sender instanceof Player))
                    return false;
                Participant ptt = core.participants.get(sender);
                switch (args[1]) {
                    case "bvi":
                        if (ptt.isPurchasedBVI) {
                            sender.sendMessage(ptt.isRussian ? "Вы уже купили это!" : "You've already purchased that!");
                            return true;
                        }
                        if (ptt.innocoinsBalance >= 800) {
                            ptt.isPurchasedBVI = true;
                            ptt.innocoinsBalance -= 800;
                            sender.sendMessage(ptt.isRussian ? "Вы купили БВИ за 800 иннокоинов\nВаш баланс: " + ptt.innocoinsBalance : "You've purchased BVI for 800 innocoins\nYour balance: " + ptt.innocoinsBalance);
                        } else {
                            sender.sendMessage(ptt.isRussian ? "У вас недостаточно средств!" : "You don't have enough innocoins!");
                        }
                        break;
                    case "ege":
                        if (ptt.isPurchasedEGE) {
                            sender.sendMessage(ptt.isRussian ? "Вы уже купили это!" : "You've already purchased that!");
                            return true;
                        }
                        if (ptt.innocoinsBalance >= 300) {
                            ptt.isPurchasedEGE = true;
                            ptt.innocoinsBalance -= 300;
                            sender.sendMessage(ptt.isRussian ? "Вы купили ЕГЭ 255+ за 300 иннокоинов\nВаш баланс: " + ptt.innocoinsBalance : "You've purchased EGE 255+ for 300 innocoins\nYour balance: " + ptt.innocoinsBalance);
                        } else {
                            sender.sendMessage(ptt.isRussian ? "У вас недостаточно средств!" : "You don't have enough innocoins!");
                        }
                        break;
                    case "portfolio":
                        if (ptt.isPurchasedPortfolio) {
                            sender.sendMessage(ptt.isRussian ? "Вы уже купили это!" : "You've already purchased that!");
                            return true;
                        }
                        if (ptt.innocoinsBalance >= 300) {
                            ptt.isPurchasedPortfolio = true;
                            ptt.innocoinsBalance -= 300;
                            sender.sendMessage(ptt.isRussian ? "Вы купили Портфолио за 300 иннокоинов\nВаш баланс: " + ptt.innocoinsBalance : "You've purchased Portfolio for 300 innocoins\nYour balance: " + ptt.innocoinsBalance);
                        } else {
                            sender.sendMessage(ptt.isRussian ? "У вас недостаточно средств!" : "You don't have enough innocoins!");
                        }
                        break;
                    case "score":
                        if (ptt.isPurchasedScore) {
                            sender.sendMessage(ptt.isRussian ? "Вы уже купили это!" : "You've already purchased that!");
                            return true;
                        }
                        if (ptt.innocoinsBalance >= 300) {
                            ptt.isPurchasedScore = true;
                            ptt.innocoinsBalance -= 300;
                            sender.sendMessage(ptt.isRussian ? "Вы купили Тесты на фулл за 300 иннокоинов\nВаш баланс: " + ptt.innocoinsBalance : "You've purchased Tests Max for 300 innocoins\nYour balance: " + ptt.innocoinsBalance);
                        } else {
                            sender.sendMessage(ptt.isRussian ? "У вас недостаточно средств!" : "You don't have enough innocoins!");
                        }
                        break;
                    case "instruction":
                        if (ptt.isPurchasedInstruction) {
                            sender.sendMessage(ptt.isRussian ? "Вы уже купили это!" : "You've already purchased that!");
                            return true;
                        }
                        if (ptt.innocoinsBalance >= 200) {
                            ptt.isPurchasedInstruction = true;
                            ptt.innocoinsBalance -= 200;
                            sender.sendMessage(ptt.isRussian ? "Вы купили Инструкцию за 200 иннокоинов\nВаш баланс: " + ptt.innocoinsBalance : "You've purchased Instruction for 200 innocoins\nYour balance: " + ptt.innocoinsBalance);
                            if (ptt.isRussian)
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ij get item_1 " + ((Player) sender).getDisplayName());
                            else
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ij get item_2 " + ((Player) sender).getDisplayName());
                        } else {
                            sender.sendMessage(ptt.isRussian ? "У вас недостаточно средств!" : "You don't have enough innocoins!");
                        }
                        break;
                    case "badge":
                        if (ptt.isPurchasedBadge) {
                            sender.sendMessage(ptt.isRussian ? "Вы уже купили это!" : "You've already purchased that!");
                            return true;
                        }
                        if (ptt.innocoinsBalance >= 100) {
                            ptt.isPurchasedBadge = true;
                            ptt.innocoinsBalance -= 100;
                            sender.sendMessage(ptt.isRussian ? "Вы купили Бейдж за 100 иннокоинов\nВаш баланс: " + ptt.innocoinsBalance : "You've purchased Badge for 100 innocoins\nYour balance: " + ptt.innocoinsBalance);
                        } else {
                            sender.sendMessage(ptt.isRussian ? "У вас недостаточно средств!" : "You don't have enough innocoins!");
                        }
                        break;
                    case "merch":
                        if (ptt.isPurchasedMerch) {
                            sender.sendMessage(ptt.isRussian ? "Вы уже купили это!" : "You've already purchased that!");
                            return true;
                        }
                        if (ptt.innocoinsBalance >= 100) {
                            ptt.isPurchasedMerch = true;
                            ptt.innocoinsBalance -= 100;
                            sender.sendMessage(ptt.isRussian ? "Вы купили Мерч за 100 иннокоинов\nВаш баланс: " + ptt.innocoinsBalance : "You've purchased Merchandise for 100 innocoins\nYour balance: " + ptt.innocoinsBalance);
                        } else {
                            sender.sendMessage(ptt.isRussian ? "У вас недостаточно средств!" : "You don't have enough innocoins!");
                        }
                        break;
                }
                ptt.updatePurchased();
                ptt.updateScores();
                return true;
        }
        return false;
    }

}
