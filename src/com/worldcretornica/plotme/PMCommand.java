package com.worldcretornica.plotme;

import com.worldcretornica.plotme.utils.MinecraftFontWidthCalculator;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PMCommand implements CommandExecutor {
	private final ChatColor BLUE = ChatColor.BLUE;
	private final ChatColor RED = ChatColor.RED;
	private final ChatColor RESET = ChatColor.RESET;
	private final ChatColor AQUA = ChatColor.AQUA;
	private final ChatColor GREEN = ChatColor.GREEN;
	private final ChatColor ITALIC = ChatColor.ITALIC;
	private final String PREFIX = PlotMe.PREFIX;
	private final String prefixe = "[Event] ";
	private final PlotMe plugin;

	public PMCommand(PlotMe instance) {
		plugin = instance;
	}

	private String C(String caption) {
		return PlotMe.caption(caption);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("plotme") || label.equalsIgnoreCase("plot") || label.equalsIgnoreCase("p")) {
			if (!(sender instanceof Player)) {
				if (args.length == 0 || args[0].equalsIgnoreCase("1")) {
					sender.sendMessage(C("ConsoleHelpMain"));
					sender.sendMessage(" - /plotme reload");
					sender.sendMessage(C("ConsoleHelpReload"));
					return true;
				} else {
					String a0 = args[0];
					if (a0.equalsIgnoreCase("reload")) {
						return reload(sender);
					}
					if (a0.equalsIgnoreCase(C("CommandResetExpired"))) {
						return resetexpired(sender, args);
					}
				}
			} else {
				Player p = (Player) sender;

				if (args.length == 0) {
					return showhelp(p, 1);
				} else {
					String a0 = args[0];
					int ipage = -1;

					try {
						ipage = Integer.parseInt(a0);
					} catch (Exception e) {
					}

					if (ipage != -1) {
						return showhelp(p, ipage);
					} else {
						if (a0.equalsIgnoreCase(C("CommandHelp"))) {
							ipage = -1;

							if (args.length > 1) {
								String a1 = args[1];
								ipage = -1;

								try {
									ipage = Integer.parseInt(a1);
								} catch (Exception e) {
								}
							}

							if (ipage != -1) {
								return showhelp(p, ipage);
							} else {
								return showhelp(p, 1);
							}
						}
						if (a0.equalsIgnoreCase(C("CommandClaim"))) {
							return claim(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandAuto"))) {
							return auto(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandInfo")) || a0.equalsIgnoreCase("i")) {
							return info(p);
						}
						if (a0.equalsIgnoreCase(C("CommandComment"))) {
							return comment(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandComments")) || a0.equalsIgnoreCase("c")) {
							return comments(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandBiome")) || a0.equalsIgnoreCase("b")) {
							return biome(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandBiomelist"))) {
							return biomelist(p);
						}
						if (a0.equalsIgnoreCase(C("CommandId"))) {
							return id(p);
						}
						if (a0.equalsIgnoreCase(C("CommandTp"))) {
							return tp(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandClear"))) {
							return clear(p);
						}
						if (a0.equalsIgnoreCase(C("CommandReset"))) {
							return reset(p);
						}
						if (a0.equalsIgnoreCase(C("CommandAdd")) || a0.equalsIgnoreCase("+")) {
							return add(p, args);
						}
						if (PlotMe.allowToDeny) {
							if (a0.equalsIgnoreCase(C("CommandDeny"))) {
								return deny(p, args);
							}
							if (a0.equalsIgnoreCase(C("CommandUndeny"))) {
								return undeny(p, args);
							}
						}
						if (a0.equalsIgnoreCase(C("CommandRemove")) || a0.equalsIgnoreCase("-")) {
							return remove(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandSetowner")) || a0.equalsIgnoreCase("o")) {
							return setowner(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandMove")) || a0.equalsIgnoreCase("m")) {
							return move(p, args);
						}
						if (a0.equalsIgnoreCase("reload")) {
							return reload(sender);
						}
						if (a0.equalsIgnoreCase(C("CommandList"))) {
							return plotlist(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandExpired"))) {
							return expired(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandAddtime"))) {
							return addtime(p);
						}
						if (a0.equalsIgnoreCase(C("CommandDone"))) {
							return done(p);
						}
						if (a0.equalsIgnoreCase(C("CommandDoneList"))) {
							return donelist(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandProtect"))) {
							return protect(p);
						}

						if (a0.equalsIgnoreCase(C("CommandSell"))) {
							return sell(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandDispose"))) {
							return dispose(p);
						}
						if (a0.equalsIgnoreCase(C("CommandAuction"))) {
							return auction(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandBuy"))) {
							return buy(p);
						}
						if (a0.equalsIgnoreCase(C("CommandBid"))) {
							return bid(p, args);
						}
						if (a0.startsWith(C("CommandHome")) || a0.startsWith("h")) {
							return home(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandResetExpired"))) {
							return resetexpired(p, args);
						}
					}
				}
			}
		}
		return false;
	}

	private boolean resetexpired(CommandSender sender, String[] args) {
		if (sender.hasPermission("PlotMe.admin.resetexpired")) {
			if (args.length <= 1) {
				Send(sender, C("WordUsage") + ": " + RED + "/plotme " + C("CommandResetExpired") + " <" + C("WordWorld") + "> " + RESET + "Example: " + RED + "/plotme " + C("CommandResetExpired") + " plotworld ");
			} else {
				if (PlotMe.worldcurrentlyprocessingexpired != null) {
					Send(sender, PlotMe.cscurrentlyprocessingexpired.getName() + " " + C("MsgAlreadyProcessingPlots"));
				} else {
					World w = sender.getServer().getWorld(args[1]);

					if (w == null) {
						Send(sender, RED + C("WordWorld") + " '" + args[1] + "' " + C("MsgDoesNotExistOrNotLoaded"));
						return true;
					} else {
						if (!PlotManager.isPlotWorld(w)) {
							Send(sender, RED + C("MsgNotPlotWorld"));
							return true;
						} else {
							PlotMe.worldcurrentlyprocessingexpired = w;
							PlotMe.cscurrentlyprocessingexpired = sender;
							PlotMe.counterexpired = 50;
							PlotMe.nbperdeletionprocessingexpired = 5;

							plugin.scheduleTask(new PlotRunnableDeleteExpire());
						}
					}
				}
			}
		} else {
			Send(sender, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean bid(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			if (p.hasPermission("PlotMe.use.bid")) {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot.auctionned) {
							String bidder = p.getName();

							if (plot.owner.equalsIgnoreCase(bidder)) {
								Send(p, RED + C("MsgCannotBidOwnPlot"));
							} else {
								if (args.length == 2) {
									double bid = 0;
									double currentbid = plot.currentbid;
									String currentbidder = plot.currentbidder;

									try {
										bid = Double.parseDouble(args[1]);
									} catch (Exception e) {
									}

									if (bid < currentbid || (bid == currentbid && !currentbidder.isEmpty())) {
										Send(p, RED + C("MsgInvalidBidMustBeAbove") + " " + RESET + f(plot.currentbid, false));
									} else {
										double balance = PlotMe.economy.getBalance(bidder);

										if (bid >= balance && !currentbidder.equals(bidder) || currentbidder.equals(bidder) && bid > (balance + currentbid)) {
											Send(p, RED + C("MsgNotEnoughBid"));
										} else {
											EconomyResponse er = PlotMe.economy.withdrawPlayer(bidder, bid);

											if (er.transactionSuccess()) {
												if (!currentbidder.isEmpty()) {
													EconomyResponse er2 = PlotMe.economy.depositPlayer(currentbidder, currentbid);

													if (!er2.transactionSuccess()) {
														Send(p, er2.errorMessage);
														warn(er2.errorMessage);
													} else {
														for (Player player : Bukkit.getServer().getOnlinePlayers()) {
															if (player.getName().equalsIgnoreCase(currentbidder)) {
																Send(player, C("MsgOutbidOnPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + ". " + f(bid));
																break;
															}
														}
													}
												}

												plot.currentbidder = bidder;
												plot.currentbid = bid;

												plot.updateField("currentbidder", bidder);
												plot.updateField("currentbid", bid);

												PlotManager.setSellSign(p.getWorld(), plot);

												Send(p, C("MsgBidAccepted") + " " + f(-bid));

												PlotMe.logger.info(prefixe + bidder + " bid " + bid + " on plot " + id);
											} else {
												Send(p, er.errorMessage);
												warn(er.errorMessage);
											}
										}
									}
								} else {
									Send(p, C("WordUsage") + ": " + RED + "/plotme " +
											C("CommandBid") + " <" + C("WordAmount") + "> " +
											RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandBid") + " 100");
								}
							}
						} else {
							Send(p, RED + C("MsgPlotNotAuctionned"));
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			} else {
				Send(p, RED + C("MsgPermissionDenied"));
			}
		} else {
			Send(p, RED + C("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean buy(Player p) {
		if (PlotManager.isEconomyEnabled(p)) {
			if (p.hasPermission("PlotMe.use.buy") || p.hasPermission("PlotMe.admin.buy")) {
				Location l = p.getLocation();
				String id = PlotManager.getPlotId(l);

				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (!plot.forsale) {
							Send(p, RED + C("MsgPlotNotForSale"));
						} else {
							String buyer = p.getName();

							if (plot.owner.equalsIgnoreCase(buyer)) {
								Send(p, RED + C("MsgCannotBuyOwnPlot"));
							} else {
								int plotlimit = PlotMe.getPlotLimit(p);

								if (plotlimit != -1 && PlotManager.getNbOwnedPlot(p) >= plotlimit) {
									Send(p, C("MsgAlreadyReachedMaxPlots") + " (" +
											PlotManager.getNbOwnedPlot(p) + "/" + PlotMe.getPlotLimit(p) + "). " +
											C("WordUse") + " " + RED + "/plotme " + C("CommandHome") + RESET + " " + C("MsgToGetToIt"));
								} else {
									World w = p.getWorld();

									double cost = plot.customprice;

									if (PlotMe.economy.getBalance(buyer) < cost) {
										Send(p, RED + C("MsgNotEnoughBuy"));
									} else {
										EconomyResponse er = PlotMe.economy.withdrawPlayer(buyer, cost);

										if (er.transactionSuccess()) {
											String oldowner = plot.owner;

											if (!oldowner.equalsIgnoreCase("$Bank$")) {
												EconomyResponse er2 = PlotMe.economy.depositPlayer(oldowner, cost);

												if (!er2.transactionSuccess()) {
													Send(p, RED + er2.errorMessage);
													warn(er2.errorMessage);
												} else {
													for (Player player : Bukkit.getServer().getOnlinePlayers()) {
														if (player.getName().equalsIgnoreCase(oldowner)) {
															Send(player, C("WordPlot") + " " + id + " " +
																	C("MsgSoldTo") + " " + buyer + ". " + f(cost));
															break;
														}
													}
												}
											}

											plot.owner = buyer;
											plot.customprice = 0;
											plot.forsale = false;

											plot.updateField("owner", buyer);
											plot.updateField("customprice", 0);
											plot.updateField("forsale", false);

											PlotManager.adjustWall(l);
											PlotManager.setSellSign(w, plot);
											PlotManager.setOwnerSign(w, plot);

											Send(p, C("MsgPlotBought") + " " + f(-cost));

											PlotMe.logger.info(prefixe + buyer + " " + C("MsgBoughtPlot") + " " + id + " " + C("WordFor") + " " + cost);
										} else {
											Send(p, RED + er.errorMessage);
											warn(er.errorMessage);
										}
									}
								}
							}
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			} else {
				Send(p, RED + C("MsgPermissionDenied"));
			}
		} else {
			Send(p, RED + C("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean auction(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			PlotMapInfo pmi = PlotManager.getMap(p);

			if (pmi.CanPutOnSale) {
				if (p.hasPermission("PlotMe.use.auction") || p.hasPermission("PlotMe.admin.auction")) {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						Send(p, RED + C("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							String name = p.getName();

							if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.auction")) {
								World w = p.getWorld();

								if (plot.auctionned) {
									if (plot.currentbidder != null && !plot.currentbidder.equalsIgnoreCase("") && !p.hasPermission("PlotMe.admin.auction")) {
										Send(p, RED + C("MsgPlotHasBidsAskAdmin"));
									} else {
										if (plot.currentbidder != null && !plot.currentbidder.equalsIgnoreCase("")) {
											EconomyResponse er = PlotMe.economy.depositPlayer(plot.currentbidder, plot.currentbid);

											if (!er.transactionSuccess()) {
												Send(p, RED + er.errorMessage);
												warn(er.errorMessage);
											} else {
												for (Player player : Bukkit.getServer().getOnlinePlayers()) {
													if (plot.currentbidder != null && player.getName().equalsIgnoreCase(plot.currentbidder)) {
														Send(player, C("MsgAuctionCancelledOnPlot") +
																" " + id + " " + C("MsgOwnedBy") + " " + plot.owner + ". " + f(plot.currentbid));
														break;
													}
												}
											}
										}

										plot.auctionned = false;
										PlotManager.adjustWall(p.getLocation());
										PlotManager.setSellSign(w, plot);
										plot.currentbid = 0;
										plot.currentbidder = "";

										plot.updateField("currentbid", 0);
										plot.updateField("currentbidder", "");
										plot.updateField("auctionned", false);

										Send(p, C("MsgAuctionCancelled"));

										PlotMe.logger.info(prefixe + name + " " + C("MsgStoppedTheAuctionOnPlot") + " " + id);
									}
								} else {
									double bid = 1;

									if (args.length == 2) {
										try {
											bid = Double.parseDouble(args[1]);
										} catch (Exception e) {
										}
									}

									if (bid < 0) {
										Send(p, RED + C("MsgInvalidAmount"));
									} else {
										plot.currentbid = bid;
										plot.auctionned = true;
										PlotManager.adjustWall(p.getLocation());
										PlotManager.setSellSign(w, plot);

										plot.updateField("currentbid", bid);
										plot.updateField("auctionned", true);

										Send(p, C("MsgAuctionStarted"));

										PlotMe.logger.info(prefixe + name + " " + C("MsgStartedAuctionOnPlot") + " " + id + " " + C("WordAt") + " " + bid);
									}
								}
							} else {
								Send(p, RED + C("MsgDoNotOwnPlot"));
							}
						} else {
							Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
						}
					}
				} else {
					Send(p, RED + C("MsgPermissionDenied"));
				}
			} else {
				Send(p, RED + C("MsgSellingPlotsIsDisabledWorld"));
			}
		} else {
			Send(p, RED + C("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean dispose(Player p) {
		if (p.hasPermission("PlotMe.admin.dispose") || p.hasPermission("PlotMe.use.dispose")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot.protect) {
							Send(p, RED + C("MsgPlotProtectedNotDisposed"));
						} else {
							String name = p.getName();

							if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.dispose")) {
								PlotMapInfo pmi = PlotManager.getMap(p);

								double cost = pmi.DisposePrice;

								if (PlotManager.isEconomyEnabled(p)) {
									if (cost != 0 && PlotMe.economy.getBalance(name) < cost) {
										Send(p, RED + C("MsgNotEnoughDispose"));
										return true;
									}

									EconomyResponse er = PlotMe.economy.withdrawPlayer(name, cost);

									if (!er.transactionSuccess()) {
										Send(p, RED + er.errorMessage);
										warn(er.errorMessage);
										return true;
									}

									if (plot.auctionned) {
										String currentbidder = plot.currentbidder;

										if (!currentbidder.isEmpty()) {
											EconomyResponse er2 = PlotMe.economy.depositPlayer(currentbidder, plot.currentbid);

											if (!er2.transactionSuccess()) {
												Send(p, RED + er2.errorMessage);
												warn(er2.errorMessage);
											} else {
												for (Player player : Bukkit.getServer().getOnlinePlayers()) {
													if (player.getName().equalsIgnoreCase(currentbidder)) {
														Send(player, C("WordPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + " " + C("MsgWasDisposed") + " " + f(cost));
														break;
													}
												}
											}
										}
									}
								}

								World w = p.getWorld();

								if (!PlotManager.isPlotAvailable(id, p)) {
									PlotManager.getPlots(p).remove(id);
								}

								PlotManager.removeOwnerSign(w, id);
								PlotManager.removeSellSign(w, id);

								SqlManager.deletePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), w.getName().toLowerCase());

								Send(p, C("MsgPlotDisposedAnyoneClaim"));

								PlotMe.logger.info(prefixe + name + " " + C("MsgDisposedPlot") + " " + id);
							} else {
								Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursCannotDispose"));
							}
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean sell(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			PlotMapInfo pmi = PlotManager.getMap(p);

			if (pmi.CanSellToBank || pmi.CanPutOnSale) {
				if (p.hasPermission("PlotMe.use.sell") || p.hasPermission("PlotMe.admin.sell")) {
					Location l = p.getLocation();
					String id = PlotManager.getPlotId(l);

					if (id.isEmpty()) {
						Send(p, RED + C("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							if (plot.owner.equalsIgnoreCase(p.getName()) || p.hasPermission("PlotMe.admin.sell")) {
								World w = p.getWorld();
								String name = p.getName();

								if (plot.forsale) {
									plot.customprice = 0;
									plot.forsale = false;

									plot.updateField("customprice", 0);
									plot.updateField("forsale", false);

									PlotManager.adjustWall(l);
									PlotManager.setSellSign(w, plot);

									Send(p, C("MsgPlotNoLongerSale"));

									PlotMe.logger.info(prefixe + name + " " + C("MsgRemovedPlot") + " " + id + " " + C("MsgFromBeingSold"));
								} else {
									double price = pmi.SellToPlayerPrice;
									boolean bank = false;

									if (args.length == 2) {
										if (args[1].equalsIgnoreCase("bank")) {
											bank = true;
										} else {
											if (pmi.CanCustomizeSellPrice) {
												try {
													price = Double.parseDouble(args[1]);
												} catch (Exception e) {
													if (pmi.CanSellToBank) {
														Send(p, C("WordUsage") + ": " + RED + " /plotme " + C("CommandSellBank") + "|<" + C("WordAmount") + ">");
														p.sendMessage("  " + C("WordExample") + ": " + RED + "/plotme " + C("CommandSellBank") + " " + RESET + " or " + RED + " /plotme " + C("CommandSell") + " 200");
													} else {
														Send(p, C("WordUsage") + ": " + RED +
																" /plotme " + C("CommandSell") + " <" + C("WordAmount") + ">" + RESET +
																" " + C("WordExample") + ": " + RED + "/plotme " + C("CommandSell") + " 200");
													}
												}
											} else {
												Send(p, RED + C("MsgCannotCustomPriceDefault") + " " + price);
												return true;
											}
										}
									}

									if (bank) {
										if (!pmi.CanSellToBank) {
											Send(p, RED + C("MsgCannotSellToBank"));
										} else {

											String currentbidder = plot.currentbidder;

											if (!currentbidder.isEmpty()) {
												double bid = plot.currentbid;

												EconomyResponse er = PlotMe.economy.depositPlayer(currentbidder, bid);

												if (!er.transactionSuccess()) {
													Send(p, RED + er.errorMessage);
													warn(er.errorMessage);
												} else {
													for (Player player : Bukkit.getServer().getOnlinePlayers()) {
														if (player.getName().equalsIgnoreCase(currentbidder)) {
															Send(player, C("WordPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + " " + C("MsgSoldToBank") + " " + f(bid));
															break;
														}
													}
												}
											}

											double sellprice = pmi.SellToBankPrice;

											EconomyResponse er = PlotMe.economy.depositPlayer(name, sellprice);

											if (er.transactionSuccess()) {
												plot.owner = "$Bank$";
												plot.forsale = true;
												plot.customprice = pmi.BuyFromBankPrice;
												plot.auctionned = false;
												plot.currentbidder = "";
												plot.currentbid = 0;

												plot.removeAllAllowed();

												PlotManager.setOwnerSign(w, plot);
												PlotManager.setSellSign(w, plot);

												plot.updateField("owner", plot.owner);
												plot.updateField("forsale", true);
												plot.updateField("auctionned", true);
												plot.updateField("customprice", plot.customprice);
												plot.updateField("currentbidder", "");
												plot.updateField("currentbid", 0);

												Send(p, C("MsgPlotSold") + " " + f(sellprice));

												PlotMe.logger.info(prefixe + name + " " + C("MsgSoldToBankPlot") + " " + id + " " + C("WordFor") + " " + sellprice);
											} else {
												Send(p, " " + er.errorMessage);
												warn(er.errorMessage);
											}
										}
									} else {
										if (price < 0) {
											Send(p, RED + C("MsgInvalidAmount"));
										} else {
											plot.customprice = price;
											plot.forsale = true;

											plot.updateField("customprice", price);
											plot.updateField("forsale", true);

											PlotManager.adjustWall(l);
											PlotManager.setSellSign(w, plot);

											Send(p, C("MsgPlotForSale"));

											PlotMe.logger.info(prefixe + name + " " + C("MsgPutOnSalePlot") + " " + id + " " + C("WordFor") + " " + price);
										}
									}
								}
							} else {
								Send(p, RED + C("MsgDoNotOwnPlot"));
							}
						} else {
							Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
						}
					}
				} else {
					Send(p, RED + C("MsgPermissionDenied"));
				}
			} else {
				Send(p, RED + C("MsgSellingPlotsIsDisabledWorld"));
			}
		} else {
			Send(p, RED + C("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean protect(Player p) {
		if (p.hasPermission("PlotMe.admin.protect") || p.hasPermission("PlotMe.use.protect")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						String name = p.getName();

						if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.protect")) {
							if (plot.protect) {
								plot.protect = false;
								PlotManager.adjustWall(p.getLocation());

								plot.updateField("protected", false);

								Send(p, C("MsgPlotNoLongerProtected"));

								PlotMe.logger.info(prefixe + name + " " + C("MsgUnprotectedPlot") + " " + id);
							} else {
								PlotMapInfo pmi = PlotManager.getMap(p);

								double cost = 0;

								if (PlotManager.isEconomyEnabled(p)) {
									cost = pmi.ProtectPrice;

									if (PlotMe.economy.getBalance(name) < cost) {
										Send(p, RED + C("MsgNotEnoughProtectPlot"));
										return true;
									} else {
										EconomyResponse er = PlotMe.economy.withdrawPlayer(name, cost);

										if (!er.transactionSuccess()) {
											Send(p, RED + er.errorMessage);
											warn(er.errorMessage);
											return true;
										}
									}

								}

								plot.protect = true;
								PlotManager.adjustWall(p.getLocation());

								plot.updateField("protected", true);

								Send(p, C("MsgPlotNowProtected") + " " + f(-cost));

								PlotMe.logger.info(prefixe + name + " " + C("MsgProtectedPlot") + " " + id);

							}
						} else {
							Send(p, RED + C("MsgDoNotOwnPlot"));
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean donelist(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.done")) {
			PlotMapInfo pmi = PlotManager.getMap(p);

			if (pmi == null) {
				Send(p, RED + C("MsgNotPlotWorld"));
				return true;
			} else {

				HashMap<String, Plot> plots = pmi.plots;
				List<Plot> finishedplots = new ArrayList<>();
				int nbfinished = 0;
				int maxpage;
				int pagesize = 8;
				int page = 1;

				if (args.length == 2) {
					try {
						page = Integer.parseInt(args[1]);
					} catch (NumberFormatException ex) {
					}
				}

				for (String id : plots.keySet()) {
					Plot plot = plots.get(id);

					if (plot.finished) {
						finishedplots.add(plot);
						nbfinished++;
					}
				}

				Collections.sort(finishedplots, new PlotFinishedComparator());

				maxpage = (int) Math.ceil(((double) nbfinished / (double) pagesize));

				if (finishedplots.size() == 0) {
					Send(p, C("MsgNoPlotsFinished"));
				} else {
					Send(p, C("MsgFinishedPlotsPage") + " " + page + "/" + maxpage);

					for (int i = (page - 1) * pagesize; i < finishedplots.size() && i < (page * pagesize); i++) {
						Plot plot = finishedplots.get(i);

						String starttext = "  " + BLUE + plot.id + RESET + " -> " + plot.owner;

						int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);

						String line = starttext + whitespace(550 - textLength) + "@" + plot.finisheddate;

						p.sendMessage(line);
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean done(Player p) {
		if (p.hasPermission("PlotMe.use.done") || p.hasPermission("PlotMe.admin.done")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);
						String name = p.getName();

						if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.done")) {
							if (plot.finished) {
								plot.setUnfinished();
								Send(p, C("MsgUnmarkFinished"));

								PlotMe.logger.info(prefixe + name + " " + C("WordMarked") + " " + id + " " + C("WordFinished"));
							} else {
								plot.setFinished();
								Send(p, C("MsgMarkFinished"));

								PlotMe.logger.info(prefixe + name + " " + C("WordMarked") + " " + id + " " + C("WordUnfinished"));
							}
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean addtime(Player p) {
		if (p.hasPermission("PlotMe.admin.addtime")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot != null) {
							String name = p.getName();

							plot.resetExpire(PlotManager.getMap(p).DaysToExpiration);
							Send(p, C("MsgPlotExpirationReset"));

							PlotMe.logger.info(prefixe + name + " reset expiration on plot " + id);
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean expired(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.expired")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
				return true;
			} else {
				int pagesize = 8;
				int page = 1;
				int maxpage;
				int nbexpiredplots = 0;
				World w = p.getWorld();
				List<Plot> expiredplots = new ArrayList<>();
				HashMap<String, Plot> plots = PlotManager.getPlots(w);
				String date = PlotMe.getDate();

				if (args.length == 2) {
					try {
						page = Integer.parseInt(args[1]);
					} catch (NumberFormatException ex) {
					}
				}

				for (String id : plots.keySet()) {
					Plot plot = plots.get(id);

					if (!plot.protect && plot.expireddate != null && PlotMe.getDate(plot.expireddate).compareTo(date) < 0) {
						nbexpiredplots++;
						expiredplots.add(plot);
					}
				}

				Collections.sort(expiredplots);

				maxpage = (int) Math.ceil(((double) nbexpiredplots / (double) pagesize));

				if (expiredplots.size() == 0) {
					Send(p, C("MsgNoPlotExpired"));
				} else {
					Send(p, C("MsgExpiredPlotsPage") + " " + page + "/" + maxpage);

					for (int i = (page - 1) * pagesize; i < expiredplots.size() && i < (page * pagesize); i++) {
						Plot plot = expiredplots.get(i);

						String starttext = "  " + BLUE + plot.id + RESET + " -> " + plot.owner;

						int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);

						String line = starttext + whitespace(550 - textLength) + "@" + plot.expireddate.toString();

						p.sendMessage(line);
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}


	private boolean plotlist(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.list")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
				return true;
			} else {
				String name;
				String pname = p.getName();

				if (p.hasPermission("PlotMe.admin.list") && args.length == 2) {
					name = args[1];
					Send(p, C("MsgListOfPlotsWhere") + " " + BLUE + name + RESET + " " + C("MsgCanBuild"));
				} else {
					name = p.getName();
					Send(p, C("MsgListOfPlotsWhereYou"));
				}

				for (Plot plot : PlotManager.getPlots(p).values()) {
					StringBuilder addition = new StringBuilder();

					if (plot.expireddate != null) {
						java.util.Date tempdate = plot.expireddate;

						if (tempdate.compareTo(Calendar.getInstance().getTime()) < 0) {
							addition.append(RED + " @" + plot.expireddate.toString() + RESET);
						} else {
							addition.append(" @" + plot.expireddate.toString());
						}
					}

					if (plot.auctionned) {
						addition.append(" " + C("WordAuction") + ": " + GREEN + round(plot.currentbid) + RESET + ((plot.currentbidder != null && !plot.currentbidder.isEmpty()) ? " " + plot.currentbidder : ""));
					}

					if (plot.forsale) {
						addition.append(" " + C("WordSell") + ": " + GREEN + round(plot.customprice) + RESET);
					}

					if (plot.owner.equalsIgnoreCase(name)) {
						if (plot.allowedcount() == 0) {
							if (name.equalsIgnoreCase(pname)) {
								p.sendMessage("  " + plot.id + " -> " + BLUE + ITALIC + C("WordYours") + RESET + addition);
							} else {
								p.sendMessage("  " + plot.id + " -> " + BLUE + ITALIC + plot.owner + RESET + addition);
							}
						} else {
							StringBuilder helpers = new StringBuilder();
							for (int i = 0; i < plot.allowedcount(); i++) {
								helpers.append(BLUE).append(plot.allowed().toArray()[i]).append(RESET).append(", ");
							}
							if (helpers.length() > 2) {
								helpers.delete(helpers.length() - 2, helpers.length());
							}

							if (name.equalsIgnoreCase(pname)) {
								p.sendMessage("  " + plot.id + " -> " + BLUE + ITALIC + C("WordYours") + RESET + addition + ", " + C("WordHelpers") + ": " + helpers);
							} else {
								p.sendMessage("  " + plot.id + " -> " + BLUE + ITALIC + plot.owner + RESET + addition + ", " + C("WordHelpers") + ": " + helpers);
							}
						}
					} else if (plot.isAllowedConsulting(name)) {
						StringBuilder helpers = new StringBuilder();
						for (int i = 0; i < plot.allowedcount(); i++) {
							if (p.getName().equalsIgnoreCase((String) plot.allowed().toArray()[i])) {
								if (name.equalsIgnoreCase(pname)) {
									helpers.append(BLUE).append(ITALIC).append("You").append(RESET).append(", ");
								} else {
									helpers.append(BLUE).append(ITALIC).append(args[1]).append(RESET).append(", ");
								}
							} else {
								helpers.append(BLUE).append(plot.allowed().toArray()[i]).append(RESET).append(", ");
							}
						}
						if (helpers.length() > 2) {
							helpers.delete(helpers.length() - 2, helpers.length());
						}

						if (plot.owner.equalsIgnoreCase(pname)) {
							p.sendMessage("  " + plot.id + " -> " + BLUE + C("WordYours") + RESET + addition + ", " + C("WordHelpers") + ": " + helpers);
						} else {
							p.sendMessage("  " + plot.id + " -> " + BLUE + plot.owner + C("WordPossessive") + RESET + addition + ", " + C("WordHelpers") + ": " + helpers);
						}
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean showhelp(Player player, int page) {
		int max = 4;
		int maxpage;
		boolean ecoon = PlotManager.isEconomyEnabled(player);

		List<String> allowed_commands = new ArrayList<>();

		allowed_commands.add("limit");
		if (player.hasPermission("PlotMe.use.claim")) {
			allowed_commands.add("claim");
		}
		if (player.hasPermission("PlotMe.use.claim.other")) {
			allowed_commands.add("claim.other");
		}
		if (player.hasPermission("PlotMe.use.auto")) {
			allowed_commands.add("auto");
		}
		if (player.hasPermission("PlotMe.use.home")) {
			allowed_commands.add("home");
		}
		if (player.hasPermission("PlotMe.use.home.other")) {
			allowed_commands.add("home.other");
		}
		if (player.hasPermission("PlotMe.use.info")) {
			allowed_commands.add("info");
			allowed_commands.add("biomeinfo");
		}
		if (player.hasPermission("PlotMe.use.comment")) {
			allowed_commands.add("comment");
		}
		if (player.hasPermission("PlotMe.use.comments")) {
			allowed_commands.add("comments");
		}
		if (player.hasPermission("PlotMe.use.list")) {
			allowed_commands.add("list");
		}
		if (player.hasPermission("PlotMe.use.biome")) {
			allowed_commands.add("biome");
			allowed_commands.add("biomelist");
		}
		if (player.hasPermission("PlotMe.use.done") || player.hasPermission("PlotMe.admin.done")) {
			allowed_commands.add("done");
		}
		if (player.hasPermission("PlotMe.admin.done")) {
			allowed_commands.add("donelist");
		}
		if (player.hasPermission("PlotMe.admin.tp")) {
			allowed_commands.add("tp");
		}
		if (player.hasPermission("PlotMe.admin.id")) {
			allowed_commands.add("id");
		}
		if (player.hasPermission("PlotMe.use.clear") || player.hasPermission("PlotMe.admin.clear")) {
			allowed_commands.add("clear");
		}
		if (player.hasPermission("PlotMe.admin.dispose") || player.hasPermission("PlotMe.use.dispose")) {
			allowed_commands.add("dispose");
		}
		if (player.hasPermission("PlotMe.admin.reset")) {
			allowed_commands.add("reset");
		}
		if (player.hasPermission("PlotMe.use.add") || player.hasPermission("PlotMe.admin.add")) {
			allowed_commands.add("add");
		}
		if (player.hasPermission("PlotMe.use.remove") || player.hasPermission("PlotMe.admin.remove")) {
			allowed_commands.add("remove");
		}
		if (PlotMe.allowToDeny) {
			if (player.hasPermission("PlotMe.use.deny") || player.hasPermission("PlotMe.admin.deny")) {
				allowed_commands.add("deny");
			}
			if (player.hasPermission("PlotMe.use.undeny") || player.hasPermission("PlotMe.admin.undeny")) {
				allowed_commands.add("undeny");
			}
		}
		if (player.hasPermission("PlotMe.admin.setowner")) {
			allowed_commands.add("setowner");
		}
		if (player.hasPermission("PlotMe.admin.move")) {
			allowed_commands.add("move");
		}
		if (player.hasPermission("PlotMe.admin.weanywhere")) {
			allowed_commands.add("weanywhere");
		}
		if (player.hasPermission("PlotMe.admin.reload")) {
			allowed_commands.add("reload");
		}
		if (player.hasPermission("PlotMe.admin.list")) {
			allowed_commands.add("listother");
		}
		if (player.hasPermission("PlotMe.admin.expired")) {
			allowed_commands.add("expired");
		}
		if (player.hasPermission("PlotMe.admin.addtime")) {
			allowed_commands.add("addtime");
		}
		if (player.hasPermission("PlotMe.admin.resetexpired")) {
			allowed_commands.add("resetexpired");
		}

		PlotMapInfo pmi = PlotManager.getMap(player);

		if (PlotManager.isPlotWorld(player) && ecoon) {
			if (player.hasPermission("PlotMe.use.buy")) {
				allowed_commands.add("buy");
			}
			if (player.hasPermission("PlotMe.use.sell")) {
				allowed_commands.add("sell");
				if (pmi.CanSellToBank) {
					allowed_commands.add("sellbank");
				}
			}
			if (player.hasPermission("PlotMe.use.auction")) {
				allowed_commands.add("auction");
			}
			if (player.hasPermission("PlotMe.use.bid")) {
				allowed_commands.add("bid");
			}
		}

		maxpage = (int) Math.ceil((double) allowed_commands.size() / max);

		if (page > maxpage) {
			page = 1;
		}

		player.sendMessage(RED + " ---==" + BLUE + C("HelpTitle") + " " + page + "/" + maxpage + RED + "==--- ");

		for (int ctr = (page - 1) * max; ctr < (page * max) && ctr < allowed_commands.size(); ctr++) {
			String allowedcmd = allowed_commands.get(ctr);

			if (allowedcmd.equalsIgnoreCase("limit")) {
				if (PlotManager.isPlotWorld(player) || PlotMe.allowWorldTeleport) {
					World w = null;

					if (PlotManager.isPlotWorld(player)) {
						w = player.getWorld();
					} else if (PlotMe.allowWorldTeleport) {
						w = PlotManager.getFirstWorld();
					}

					int maxplots = PlotMe.getPlotLimit(player);
					int ownedplots = PlotManager.getNbOwnedPlot(player, w);

					if (maxplots == -1) {
						player.sendMessage(GREEN + C("HelpYourPlotLimitWorld") + " : " + AQUA + ownedplots +
								GREEN + " " + C("HelpUsedOf") + " " + AQUA + C("WordInfinite"));
					} else {
						player.sendMessage(GREEN + C("HelpYourPlotLimitWorld") + " : " + AQUA + ownedplots +
								GREEN + " " + C("HelpUsedOf") + " " + AQUA + maxplots);
					}
				} else {
					player.sendMessage(GREEN + C("HelpYourPlotLimitWorld") + " : " + AQUA + C("MsgNotPlotWorld"));
				}
			} else if (allowedcmd.equalsIgnoreCase("claim")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandClaim"));
				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpClaim") + " " + C("WordPrice") + " : " + RESET + round(pmi.ClaimPrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpClaim"));
				}
			} else if (allowedcmd.equalsIgnoreCase("claim.other")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandClaim") + " <" + C("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpClaimOther") + " " + C("WordPrice") + " : " + RESET + round(pmi.ClaimPrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpClaimOther"));
				}
			} else if (allowedcmd.equalsIgnoreCase("auto")) {
				if (PlotMe.allowWorldTeleport) {
					player.sendMessage(GREEN + " /plotme " + C("CommandAuto") + " [" + C("WordWorld") + "]");
				} else {
					player.sendMessage(GREEN + " /plotme " + C("CommandAuto"));
				}

				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpAuto") + " " + C("WordPrice") + " : " + RESET + round(pmi.ClaimPrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpAuto"));
				}
			} else if (allowedcmd.equalsIgnoreCase("home")) {
				if (PlotMe.allowWorldTeleport) {
					player.sendMessage(GREEN + " /plotme " + C("CommandHome") + "[:#] [" + C("WordWorld") + "]");
				} else {
					player.sendMessage(GREEN + " /plotme " + C("CommandHome") + "[:#]");
				}

				if (ecoon && pmi != null && pmi.PlotHomePrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpHome") + " " + C("WordPrice") + " : " + RESET + round(pmi.PlotHomePrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpHome"));
				}
			} else if (allowedcmd.equalsIgnoreCase("home.other")) {
				if (PlotMe.allowWorldTeleport) {
					player.sendMessage(GREEN + " /plotme " + C("CommandHome") + "[:#] <" + C("WordPlayer") + "> [" + C("WordWorld") + "]");
				} else {
					player.sendMessage(GREEN + " /plotme " + C("CommandHome") + "[:#] <" + C("WordPlayer") + ">");
				}

				if (ecoon && pmi != null && pmi.PlotHomePrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpHomeOther") + " " + C("WordPrice") + " : " + RESET + round(pmi.PlotHomePrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpHomeOther"));
				}
			} else if (allowedcmd.equalsIgnoreCase("info")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandInfo"));
				player.sendMessage(AQUA + " " + C("HelpInfo"));
			} else if (allowedcmd.equalsIgnoreCase("comment")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandComment") + " <" + C("WordComment") + ">");
				if (ecoon && pmi != null && pmi.AddCommentPrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpComment") + " " + C("WordPrice") + " : " + RESET + round(pmi.AddCommentPrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpComment"));
				}
			} else if (allowedcmd.equalsIgnoreCase("comments")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandComments"));
				player.sendMessage(AQUA + " " + C("HelpComments"));
			} else if (allowedcmd.equalsIgnoreCase("list")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandList"));
				player.sendMessage(AQUA + " " + C("HelpList"));
			} else if (allowedcmd.equalsIgnoreCase("listother")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandList") + " <" + C("WordPlayer") + ">");
				player.sendMessage(AQUA + " " + C("HelpListOther"));
			} else if (allowedcmd.equalsIgnoreCase("biomeinfo")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandBiome"));
				player.sendMessage(AQUA + " " + C("HelpBiomeInfo"));
			} else if (allowedcmd.equalsIgnoreCase("biome")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandBiome") + " <" + C("WordBiome") + ">");
				if (ecoon && pmi != null && pmi.BiomeChangePrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpBiome") + " " + C("WordPrice") + " : " + RESET + round(pmi.BiomeChangePrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpBiome"));
				}
			} else if (allowedcmd.equalsIgnoreCase("biomelist")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandBiomelist"));
				player.sendMessage(AQUA + " " + C("HelpBiomeList"));
			} else if (allowedcmd.equalsIgnoreCase("done")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandDone"));
				player.sendMessage(AQUA + " " + C("HelpDone"));
			} else if (allowedcmd.equalsIgnoreCase("tp")) {
				if (PlotMe.allowWorldTeleport) {
					player.sendMessage(GREEN + " /plotme " + C("CommandTp") + " <" + C("WordId") + "> [" + C("WordWorld") + "]");
				} else {
					player.sendMessage(GREEN + " /plotme " + C("CommandTp") + " <" + C("WordId") + ">");
				}

				player.sendMessage(AQUA + " " + C("HelpTp"));
			} else if (allowedcmd.equalsIgnoreCase("id")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandId"));
				player.sendMessage(AQUA + " " + C("HelpId"));
			} else if (allowedcmd.equalsIgnoreCase("clear")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandClear"));
				if (ecoon && pmi != null && pmi.ClearPrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpId") + " " + C("WordPrice") + " : " + RESET + round(pmi.ClearPrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpClear"));
				}
			} else if (allowedcmd.equalsIgnoreCase("reset")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandReset"));
				player.sendMessage(AQUA + " " + C("HelpReset"));
			} else if (allowedcmd.equalsIgnoreCase("add")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandAdd") + " <" + C("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.AddPlayerPrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpAdd") + " " + C("WordPrice") + " : " + RESET + round(pmi.AddPlayerPrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpAdd"));
				}
			} else if (allowedcmd.equalsIgnoreCase("deny")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandDeny") + " <" + C("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.DenyPlayerPrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpDeny") + " " + C("WordPrice") + " : " + RESET + round(pmi.DenyPlayerPrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpDeny"));
				}
			} else if (allowedcmd.equalsIgnoreCase("remove")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandRemove") + " <" + C("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.RemovePlayerPrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpRemove") + " " + C("WordPrice") + " : " + RESET + round(pmi.RemovePlayerPrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpRemove"));
				}
			} else if (allowedcmd.equalsIgnoreCase("undeny")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandUndeny") + " <" + C("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.UndenyPlayerPrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpUndeny") + " " + C("WordPrice") + " : " + RESET + round(pmi.UndenyPlayerPrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpUndeny"));
				}
			} else if (allowedcmd.equalsIgnoreCase("setowner")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandSetowner") + " <" + C("WordPlayer") + ">");
				player.sendMessage(AQUA + " " + C("HelpSetowner"));
			} else if (allowedcmd.equalsIgnoreCase("move")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandMove") + " <" + C("WordIdFrom") + "> <" + C("WordIdTo") + ">");
				player.sendMessage(AQUA + " " + C("HelpMove"));
			} else if (allowedcmd.equalsIgnoreCase("weanywhere")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandWEAnywhere"));
				player.sendMessage(AQUA + " " + C("HelpWEAnywhere"));
			} else if (allowedcmd.equalsIgnoreCase("expired")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandExpired") + " [page]");
				player.sendMessage(AQUA + " " + C("HelpExpired"));
			} else if (allowedcmd.equalsIgnoreCase("donelist")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandDoneList") + " [page]");
				player.sendMessage(AQUA + " " + C("HelpDoneList"));
			} else if (allowedcmd.equalsIgnoreCase("addtime")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandAddtime"));
				int days = (pmi == null) ? 0 : pmi.DaysToExpiration;
				if (days == 0) {
					player.sendMessage(AQUA + " " + C("HelpAddTime1") + " " + RESET + C("WordNever"));
				} else {
					player.sendMessage(AQUA + " " + C("HelpAddTime1") + " " + RESET + days + AQUA + " " + C("HelpAddTime2"));
				}
			} else if (allowedcmd.equalsIgnoreCase("reload")) {
				player.sendMessage(GREEN + " /plotme reload");
				player.sendMessage(AQUA + " " + C("HelpReload"));
			} else if (allowedcmd.equalsIgnoreCase("dispose")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandDispose"));
				if (ecoon && pmi != null && pmi.DisposePrice != 0) {
					player.sendMessage(AQUA + " " + C("HelpDispose") + " " + C("WordPrice") + " : " + RESET + round(pmi.DisposePrice));
				} else {
					player.sendMessage(AQUA + " " + C("HelpDispose"));
				}
			} else if (allowedcmd.equalsIgnoreCase("buy")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandBuy"));
				player.sendMessage(AQUA + " " + C("HelpBuy"));
			} else if (allowedcmd.equalsIgnoreCase("sell")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandSell") + " [" + C("WordAmount") + "]");
				player.sendMessage(AQUA + " " + C("HelpSell") + " " + C("WordDefault") + " : " + RESET + round(pmi.SellToPlayerPrice));
			} else if (allowedcmd.equalsIgnoreCase("sellbank")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandSellBank"));
				player.sendMessage(AQUA + " " + C("HelpSellBank") + " " + RESET + round(pmi.SellToBankPrice));
			} else if (allowedcmd.equalsIgnoreCase("auction")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandAuction") + " [" + C("WordAmount") + "]");
				player.sendMessage(AQUA + " " + C("HelpAuction") + " " + C("WordDefault") + " : " + RESET + "1");
			} else if (allowedcmd.equalsIgnoreCase("resetexpired")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandResetExpired") + " <" + C("WordWorld") + ">");
				player.sendMessage(AQUA + " " + C("HelpResetExpired"));
			} else if (allowedcmd.equalsIgnoreCase("bid")) {
				player.sendMessage(GREEN + " /plotme " + C("CommandBid") + " <" + C("WordAmount") + ">");
				player.sendMessage(AQUA + " " + C("HelpBid"));
			}
		}

		return true;
	}

	private boolean tp(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.tp")) {
			if (!PlotManager.isPlotWorld(p) && !PlotMe.allowWorldTeleport) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				if (args.length == 2 || (args.length == 3 && PlotMe.allowWorldTeleport)) {
					String id = args[1];

					if (!PlotManager.isValidId(id)) {
						if (PlotMe.allowWorldTeleport) {
							Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandTp") + " <" + C("WordId") + "> [" + C("WordWorld") + "] " + RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandTp") + " 5;-1 ");
						} else {
							Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandTp") + " <" + C("WordId") + "> " + RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandTp") + " 5;-1 ");
						}
						return true;
					} else {
						World w;

						if (args.length == 3) {
							String world = args[2];

							w = Bukkit.getWorld(world);
						} else {
							if (!PlotManager.isPlotWorld(p)) {
								w = PlotManager.getFirstWorld();
							} else {
								w = p.getWorld();
							}
						}

						if (w == null || !PlotManager.isPlotWorld(w)) {
							Send(p, RED + C("MsgNoPlotworldFound"));
						} else {
							Location bottom = PlotManager.getPlotBottomLoc(w, id);
							Location top = PlotManager.getPlotTopLoc(w, id);

							p.teleport(new Location(w, bottom.getX() + (top.getBlockX() - bottom.getBlockX()) / 2, PlotManager.getMap(w).RoadHeight + 2, bottom.getZ() - 2));
						}
					}
				} else {
					if (PlotMe.allowWorldTeleport) {
						Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandTp") + " <" + C("WordId") + "> [" + C("WordWorld") + "] " + RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandTp") + " 5;-1 ");
					} else {
						Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandTp") + " <" + C("WordId") + "> " + RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandTp") + " 5;-1 ");
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean auto(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.auto")) {
			if (!PlotManager.isPlotWorld(p) && !PlotMe.allowWorldTeleport) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				World w;

				if (!PlotManager.isPlotWorld(p) && PlotMe.allowWorldTeleport) {
					if (args.length == 2) {
						w = Bukkit.getWorld(args[1]);
					} else {
						w = PlotManager.getFirstWorld();
					}

					if (w == null || !PlotManager.isPlotWorld(w)) {
						Send(p, RED + args[1] + " " + C("MsgWorldNotPlot"));
						return true;
					}
				} else {
					w = p.getWorld();
				}

				if (w == null) {
					Send(p, RED + C("MsgNoPlotworldFound"));
				} else {
					if (PlotManager.getNbOwnedPlot(p, w) >= PlotMe.getPlotLimit(p) && !p.hasPermission("PlotMe.admin")) {
						Send(p, RED + C("MsgAlreadyReachedMaxPlots") + " (" +
								PlotManager.getNbOwnedPlot(p, w) + "/" + PlotMe.getPlotLimit(p) + "). " + C("WordUse") + " " + "/plotme " + C("CommandHome") + " " + C("MsgToGetToIt"));
					} else {
						PlotMapInfo pmi = PlotManager.getMap(w);
						int limit = pmi.PlotAutoLimit;

						for (int i = 0; i < limit; i++) {
							for (int x = -i; x <= i; x++) {
								for (int z = -i; z <= i; z++) {
									String id = x + ";" + z;

									if (PlotManager.isPlotAvailable(id, w)) {
										String name = p.getName();
										UUID uuid = p.getUniqueId();

										double price = 0;

										if (PlotManager.isEconomyEnabled(w)) {
											price = pmi.ClaimPrice;
											double balance = PlotMe.economy.getBalance(name);

											if (balance >= price) {
												EconomyResponse er = PlotMe.economy.withdrawPlayer(name, price);

												if (!er.transactionSuccess()) {
													Send(p, RED + er.errorMessage);
													warn(er.errorMessage);
													return true;
												}
											} else {
												Send(p, RED + C("MsgNotEnoughAuto") + " " + C("WordMissing") + " " + RESET + f(price - balance, false));
												return true;
											}
										}

										Plot plot = PlotManager.createPlot(w, id, name, uuid);

										//PlotManager.adjustLinkedPlots(id, w);

										p.teleport(new Location(w, PlotManager.bottomX(plot.id, w) + (PlotManager.topX(plot.id, w) -
												PlotManager.bottomX(plot.id, w)) / 2, pmi.RoadHeight + 2, PlotManager.bottomZ(plot.id, w) - 2));

										Send(p, C("MsgThisPlotYours") + " " + C("WordUse") + " " + RED + "/plotme " + C("CommandHome") + RESET + " " + C("MsgToGetToIt") + " " + f(-price));

										PlotMe.logger.info(prefixe + name + " " + C("MsgClaimedPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));

										return true;
									}
								}
							}
						}

						Send(p, RED + C("MsgNoPlotFound1") + " " + (limit ^ 2) + " " + C("MsgNoPlotFound2"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean claim(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.claim") || p.hasPermission("PlotMe.admin.claim.other")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, RED + C("MsgCannotClaimRoad"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					Send(p, RED + C("MsgThisPlotOwned"));
				} else {
					String playername = p.getName();
					UUID uuid = p.getUniqueId();

					if (args.length == 2) {
						if (p.hasPermission("PlotMe.admin.claim.other")) {
							playername = args[1];
							uuid = null;
						}
					}

					int plotlimit = PlotMe.getPlotLimit(p);

					if (playername.equals(p.getName()) && plotlimit != -1 && PlotManager.getNbOwnedPlot(p) >= plotlimit) {
						Send(p, RED + C("MsgAlreadyReachedMaxPlots") + " (" +
								PlotManager.getNbOwnedPlot(p) + "/" + PlotMe.getPlotLimit(p) + "). " + C("WordUse") + " " + RED + "/plotme " + C("CommandHome") + RESET + " " + C("MsgToGetToIt"));
					} else {
						World w = p.getWorld();
						PlotMapInfo pmi = PlotManager.getMap(w);

						double price = 0;

						if (PlotManager.isEconomyEnabled(w)) {
							price = pmi.ClaimPrice;
							double balance = PlotMe.economy.getBalance(playername);

							if (balance >= price) {
								EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

								if (!er.transactionSuccess()) {
									Send(p, RED + er.errorMessage);
									warn(er.errorMessage);
									return true;
								}
							} else {
								Send(p, RED + C("MsgNotEnoughBuy") + " " + C("WordMissing") + " " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
								return true;
							}
						}

						Plot plot = PlotManager.createPlot(w, id, playername, uuid);

						//PlotManager.adjustLinkedPlots(id, w);

						if (plot == null) {
							Send(p, RED + C("ErrCreatingPlotAt") + " " + id);
						} else {
							if (playername.equalsIgnoreCase(p.getName())) {
								Send(p, C("MsgThisPlotYours") + " " + C("WordUse") + " " + RED + "/plotme " + C("CommandHome") + RESET + " " + C("MsgToGetToIt") + " " + f(-price));
							} else {
								Send(p, C("MsgThisPlotIsNow") + " " + playername + C("WordPossessive") + ". " + C("WordUse") + " " + RED + "/plotme " + C("CommandHome") + RESET + " " + C("MsgToGetToIt") + " " + f(-price));
							}

							PlotMe.logger.info(prefixe + playername + " " + C("MsgClaimedPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
						}
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean home(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.home") || p.hasPermission("PlotMe.admin.home.other")) {
			if (!PlotManager.isPlotWorld(p) && !PlotMe.allowWorldTeleport) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String playername = p.getName();
				UUID uuid = p.getUniqueId();
				int nb = 1;
				World w;
				String worldname = "";

				if (!PlotManager.isPlotWorld(p) && PlotMe.allowWorldTeleport) {
					w = PlotManager.getFirstWorld();
				} else {
					w = p.getWorld();
				}

				if (w != null) {
					worldname = w.getName();
				}

				if (args[0].contains(":")) {
					try {
						if (args[0].split(":").length == 1 || args[0].split(":")[1].isEmpty()) {
							Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandHome") + ":# " +
									RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandHome") + ":1");
							return true;
						} else {
							nb = Integer.parseInt(args[0].split(":")[1]);
						}
					} catch (Exception ex) {
						Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandHome") + ":# " +
								RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandHome") + ":1");
						return true;
					}
				}

				if (args.length == 2) {
					if (Bukkit.getWorld(args[1]) == null) {
						if (p.hasPermission("PlotMe.admin.home.other")) {
							playername = args[1];
							uuid = null;
						}
					} else {
						w = Bukkit.getWorld(args[1]);
						worldname = args[1];
					}
				}

				if (args.length == 3) {
					if (Bukkit.getWorld(args[2]) == null) {
						Send(p, RED + args[2] + " " + C("MsgWorldNotPlot"));
						return true;
					} else {
						w = Bukkit.getWorld(args[2]);
						worldname = args[2];
					}
				}

				if (!PlotManager.isPlotWorld(w)) {
					Send(p, RED + worldname + " " + C("MsgWorldNotPlot"));
				} else {
					int i = nb - 1;

					for (Plot plot : PlotManager.getPlots(w).values()) {
						if (uuid == null && plot.owner.equalsIgnoreCase(playername) || uuid != null && plot.ownerId != null && plot.ownerId.equals(uuid)) {
							if (i == 0) {
								PlotMapInfo pmi = PlotManager.getMap(w);

								double price = 0;

								if (PlotManager.isEconomyEnabled(w)) {
									price = pmi.PlotHomePrice;
									double balance = PlotMe.economy.getBalance(playername);

									if (balance >= price) {
										EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

										if (!er.transactionSuccess()) {
											Send(p, RED + er.errorMessage);
											return true;
										}
									} else {
										Send(p, RED + C("MsgNotEnoughTp") + " " + C("WordMissing") + " " + RESET + f(price - balance, false));
										return true;
									}
								}

								p.teleport(PlotManager.getPlotHome(w, plot));

								if (price != 0) {
									Send(p, f(-price));
								}

								return true;
							} else {
								i--;
							}
						}
					}

					if (nb > 0) {
						if (!playername.equalsIgnoreCase(p.getName())) {
							Send(p, RED + playername + " " + C("MsgDoesNotHavePlot") + " #" + nb);
						} else {
							Send(p, RED + C("MsgPlotNotFound") + " #" + nb);
						}
					} else if (!playername.equalsIgnoreCase(p.getName())) {
						Send(p, RED + playername + " " + C("MsgDoesNotHavePlot"));
					} else {
						Send(p, RED + C("MsgYouHaveNoPlot"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean info(Player p) {
		if (p.hasPermission("PlotMe.use.info")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						p.sendMessage(GREEN + C("InfoId") + ": " + AQUA + id +
								GREEN + " " + C("InfoOwner") + ": " + AQUA + plot.owner +
								GREEN + " " + C("InfoBiome") + ": " + AQUA + FormatBiome(plot.biome.name()));

						p.sendMessage(GREEN + C("InfoExpire") + ": " + AQUA + ((plot.expireddate == null) ? C("WordNever") : plot.expireddate.toString()) +
								GREEN + " " + C("InfoFinished") + ": " + AQUA + ((plot.finished) ? C("WordYes") : C("WordNo")) +
								GREEN + " " + C("InfoProtected") + ": " + AQUA + ((plot.protect) ? C("WordYes") : C("WordNo")));

						if (plot.allowedcount() > 0) {
							p.sendMessage(GREEN + C("InfoHelpers") + ": " + AQUA + plot.getAllowed());
						}

						if (PlotMe.allowToDeny && plot.deniedcount() > 0) {
							p.sendMessage(GREEN + C("InfoDenied") + ": " + AQUA + plot.getDenied());
						}

						if (PlotManager.isEconomyEnabled(p)) {
							if (plot.currentbidder == null || plot.currentbidder.equalsIgnoreCase("")) {
								p.sendMessage(GREEN + C("InfoAuctionned") + ": " + AQUA + ((plot.auctionned) ? C("WordYes") +
										GREEN + " " + C("InfoMinimumBid") + ": " + AQUA + round(plot.currentbid) : C("WordNo")) +
										GREEN + " " + C("InfoForSale") + ": " + AQUA + ((plot.forsale) ? AQUA + round(plot.customprice) : C("WordNo")));
							} else {
								p.sendMessage(GREEN + C("InfoAuctionned") + ": " + AQUA + ((plot.auctionned) ? C("WordYes") +
										GREEN + " " + C("InfoBidder") + ": " + AQUA + plot.currentbidder +
										GREEN + " " + C("InfoBid") + ": " + AQUA + round(plot.currentbid) : C("WordNo")) +
										GREEN + " " + C("InfoForSale") + ": " + AQUA + ((plot.forsale) ? AQUA + round(plot.customprice) : C("WordNo")));
							}
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean comment(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.comment")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				if (args.length < 2) {
					Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandComment") + " <" + C("WordText") + ">");
				} else {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						Send(p, RED + C("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							World w = p.getWorld();
							PlotMapInfo pmi = PlotManager.getMap(w);
							String playername = p.getName();
							UUID uuid = p.getUniqueId();

							double price = 0;

							if (PlotManager.isEconomyEnabled(w)) {
								price = pmi.AddCommentPrice;
								double balance = PlotMe.economy.getBalance(playername);

								if (balance >= price) {
									EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

									if (!er.transactionSuccess()) {
										Send(p, RED + er.errorMessage);
										warn(er.errorMessage);
										return true;
									}
								} else {
									Send(p, RED + C("MsgNotEnoughComment") + " " + C("WordMissing") + " " + RESET + f(price - balance, false));
									return true;
								}
							}

							Plot plot = PlotManager.getPlotById(p, id);

							String text = StringUtils.join(args, " ");
							text = text.substring(text.indexOf(" "));

							String[] comment = new String[3];
							comment[0] = playername;
							comment[1] = text;
							comment[2] = uuid.toString();

							plot.comments.add(comment);
							SqlManager.addPlotComment(comment, plot.comments.size(), PlotManager.getIdX(id), PlotManager.getIdZ(id), plot.world, uuid);

							Send(p, C("MsgCommentAdded") + " " + f(-price));

							PlotMe.logger.info(prefixe + playername + " " + C("MsgCommentedPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
						} else {
							Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
						}
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean comments(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.comments")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				if (args.length < 2) {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						Send(p, RED + C("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							if (plot.ownerId.equals(p.getUniqueId()) || plot.isAllowed(p.getUniqueId()) || p.hasPermission("PlotMe.admin")) {
								if (plot.comments.size() == 0) {
									Send(p, C("MsgNoComments"));
								} else {
									Send(p, C("MsgYouHave") + " " +
											BLUE + plot.comments.size() + RESET + " " + C("MsgComments"));

									for (String[] comment : plot.comments) {
										p.sendMessage(ChatColor.BLUE + C("WordFrom") + " : " + RED + comment[0]);
										p.sendMessage("" + RESET + ChatColor.ITALIC + comment[1]);
									}

								}
							} else {
								Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedViewComments"));
							}
						} else {
							Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
						}
					}
				}
			}
		} else {
			p.sendMessage(PREFIX + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean biome(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.biome")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					p.sendMessage(PREFIX + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						World w = p.getWorld();

						if (args.length == 2) {
							Biome biome = null;

							for (Biome bio : Biome.values()) {
								if (bio.name().equalsIgnoreCase(args[1])) {
									biome = bio;
								}
							}

							if (biome == null) {
								Send(p, RED + args[1] + RESET + " " + C("MsgIsInvalidBiome"));
							} else {
								Plot plot = PlotManager.getPlotById(p, id);
								String playername = p.getName();

								if (plot.owner.equalsIgnoreCase(playername) || p.hasPermission("PlotMe.admin")) {
									PlotMapInfo pmi = PlotManager.getMap(w);

									double price = 0;

									if (PlotManager.isEconomyEnabled(w)) {
										price = pmi.BiomeChangePrice;
										double balance = PlotMe.economy.getBalance(playername);

										if (balance >= price) {
											EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

											if (!er.transactionSuccess()) {
												Send(p, RED + er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, RED + C("MsgNotEnoughBiome") + " " + C("WordMissing") + " " + RESET + f(price - balance, false));
											return true;
										}
									}

									PlotManager.setBiome(w, id, plot, biome);

									Send(p, C("MsgBiomeSet") + " " + ChatColor.BLUE + FormatBiome(biome.name()) + " " + f(-price));

									PlotMe.logger.info(prefixe + playername + " " + C("MsgChangedBiome") + " " + id + " " + C("WordTo") + " " +
											FormatBiome(biome.name()) + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
								} else {
									Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedBiome"));
								}
							}
						} else {
							Plot plot = PlotMe.plotmaps.get(w.getName().toLowerCase()).plots.get(id);

							Send(p, C("MsgPlotUsingBiome") + " " + ChatColor.BLUE + FormatBiome(plot.biome.name()));
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean biomelist(CommandSender s) {
		if (!(s instanceof Player) || s.hasPermission("PlotMe.use.biome")) {
			Send(s, C("WordBiomes") + " : ");

			//int i = 0;
			StringBuilder line = new StringBuilder();
			List<String> biomes = new ArrayList<>();

			for (Biome b : Biome.values()) {
				biomes.add(b.name());
			}

			Collections.sort(biomes);

			List<String> column1 = new ArrayList<>();
			List<String> column2 = new ArrayList<>();
			List<String> column3 = new ArrayList<>();

			for (int ctr = 0; ctr < biomes.size(); ctr++) {
				if (ctr < biomes.size() / 3) {
					column1.add(biomes.get(ctr));
				} else if (ctr < biomes.size() * 2 / 3) {
					column2.add(biomes.get(ctr));
				} else {
					column3.add(biomes.get(ctr));
				}
			}


			for (int ctr = 0; ctr < column1.size(); ctr++) {
				String b;
				int nameLength;

				b = FormatBiome(column1.get(ctr));
				nameLength = MinecraftFontWidthCalculator.getStringWidth(b);
				line.append(b).append(whitespace(432 - nameLength));

				if (ctr < column2.size()) {
					b = FormatBiome(column2.get(ctr));
					nameLength = MinecraftFontWidthCalculator.getStringWidth(b);
					line.append(b).append(whitespace(432 - nameLength));
				}

				if (ctr < column3.size()) {
					b = FormatBiome(column3.get(ctr));
					line.append(b);
				}

				s.sendMessage("" + BLUE + line);
				//i = 0;
				line = new StringBuilder();
								
				/*int nameLength = MinecraftFontWidthCalculator.getStringWidth(b);
				
				i += 1;
				if(i == 3)
				{
					line.append(b);
					s.sendMessage("" + BLUE + line);
					i = 0;
					line = new StringBuilder();
				}
				else
				{
					line.append(b).append(whitespace(318 - nameLength));
				}*/
			}
		} else {
			Send(s, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean reset(Player p) {
		if (p.hasPermission("PlotMe.admin.reset")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				Plot plot = PlotManager.getPlotById(p.getLocation());

				if (plot == null) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (plot.protect) {
						Send(p, RED + C("MsgPlotProtectedCannotReset"));
					} else {
						String id = plot.id;
						World w = p.getWorld();

						PlotManager.setBiome(w, id, plot, Biome.PLAINS);
						PlotManager.clear(w, plot);
						//RemoveLWC(w, plot);

						if (PlotManager.isEconomyEnabled(p)) {
							if (plot.auctionned) {
								String currentbidder = plot.currentbidder;

								if (!currentbidder.isEmpty()) {
									EconomyResponse er = PlotMe.economy.depositPlayer(currentbidder, plot.currentbid);

									if (!er.transactionSuccess()) {
										Send(p, er.errorMessage);
										warn(er.errorMessage);
									} else {
										for (Player player : Bukkit.getServer().getOnlinePlayers()) {
											if (player.getName().equalsIgnoreCase(currentbidder)) {
												Send(player, C("WordPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + " " + C("MsgWasReset") + " " + f(plot.currentbid));
												break;
											}
										}
									}
								}
							}

							PlotMapInfo pmi = PlotManager.getMap(p);

							if (pmi.RefundClaimPriceOnReset) {
								EconomyResponse er = PlotMe.economy.depositPlayer(plot.owner, pmi.ClaimPrice);

								if (!er.transactionSuccess()) {
									Send(p, RED + er.errorMessage);
									warn(er.errorMessage);
									return true;
								} else {
									for (Player player : Bukkit.getServer().getOnlinePlayers()) {
										if (player.getName().equalsIgnoreCase(plot.owner)) {
											Send(player, C("WordPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.owner + " " + C("MsgWasReset") + " " + f(pmi.ClaimPrice));
											break;
										}
									}
								}
							}
						}

						if (!PlotManager.isPlotAvailable(id, p)) {
							PlotManager.getPlots(p).remove(id);
						}

						String name = p.getName();

						PlotManager.removeOwnerSign(w, id);
						PlotManager.removeSellSign(w, id);

						SqlManager.deletePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), w.getName().toLowerCase());

						Send(p, C("MsgPlotReset"));

						PlotMe.logger.info(prefixe + name + " " + C("MsgResetPlot") + " " + id);
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean clear(Player p) {
		if (p.hasPermission("PlotMe.admin.clear") || p.hasPermission("PlotMe.use.clear")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot.protect) {
							Send(p, RED + C("MsgPlotProtectedCannotClear"));
						} else {
							String playername = p.getName();

							if (plot.owner.equalsIgnoreCase(playername) || p.hasPermission("PlotMe.admin.clear")) {
								World w = p.getWorld();

								PlotMapInfo pmi = PlotManager.getMap(w);

								double price = 0;

								if (PlotManager.isEconomyEnabled(w)) {
									price = pmi.ClearPrice;
									double balance = PlotMe.economy.getBalance(playername);

									if (balance >= price) {
										EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

										if (!er.transactionSuccess()) {
											Send(p, RED + er.errorMessage);
											warn(er.errorMessage);
											return true;
										}
									} else {
										Send(p, RED + C("MsgNotEnoughClear") + " " + C("WordMissing") + " " + RESET + (price - balance) + RED + " " + PlotMe.economy.currencyNamePlural());
										return true;
									}
								}

								PlotManager.clear(w, plot);
								//RemoveLWC(w, plot, p);
								//PlotManager.regen(w, plot);

								Send(p, C("MsgPlotCleared") + " " + f(-price));

								PlotMe.logger.info(prefixe + playername + " " + C("MsgClearedPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
							} else {
								Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedClear"));
							}
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean add(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.add") || p.hasPermission("PlotMe.use.add")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							Send(p, C("WordUsage") + " " + RED + "/plotme " + C("CommandAdd") + " <" + C("WordPlayer") + ">");
						} else {

							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();
							String allowed = args[1];

							if (plot.owner.equalsIgnoreCase(playername) || p.hasPermission("PlotMe.admin.add")) {
								if (plot.isAllowedConsulting(allowed) || plot.isGroupAllowed(allowed)) {
									Send(p, C("WordPlayer") + " " + RED + args[1] + RESET + " " + C("MsgAlreadyAllowed"));
								} else {
									World w = p.getWorld();

									PlotMapInfo pmi = PlotManager.getMap(w);

									double price = 0;

									if (PlotManager.isEconomyEnabled(w)) {
										price = pmi.AddPlayerPrice;
										double balance = PlotMe.economy.getBalance(playername);

										if (balance >= price) {
											EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

											if (!er.transactionSuccess()) {
												Send(p, RED + er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, RED + C("MsgNotEnoughAdd") + " " + C("WordMissing") + " " + RESET + f(price - balance, false));
											return true;
										}
									}

									plot.addAllowed(allowed);
									plot.removeDenied(allowed);

									Send(p, C("WordPlayer") + " " + RED + allowed + RESET + " " + C("MsgNowAllowed") + " " + f(-price));

									PlotMe.logger.info(prefixe + playername + " " + C("MsgAddedPlayer") + " " + allowed + " " + C("MsgToPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
								}
							} else {
								Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedAdd"));
							}
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean deny(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.deny") || p.hasPermission("PlotMe.use.deny")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							Send(p, C("WordUsage") + " " + RED + "/plotme " + C("CommandDeny") + " <" + C("WordPlayer") + ">");
						} else {

							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();
							String denied = args[1];

							if (plot.owner.equalsIgnoreCase(playername) || p.hasPermission("PlotMe.admin.deny")) {
								if (plot.isDeniedConsulting(denied) || plot.isGroupDenied(denied)) {
									Send(p, C("WordPlayer") + " " + RED + args[1] + RESET + " " + C("MsgAlreadyDenied"));
								} else {
									World w = p.getWorld();

									PlotMapInfo pmi = PlotManager.getMap(w);

									double price = 0;

									if (PlotManager.isEconomyEnabled(w)) {
										price = pmi.DenyPlayerPrice;
										double balance = PlotMe.economy.getBalance(playername);

										if (balance >= price) {
											EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

											if (!er.transactionSuccess()) {
												Send(p, RED + er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, RED + C("MsgNotEnoughDeny") + " " + C("WordMissing") + " " + RESET + f(price - balance, false));
											return true;
										}
									}

									plot.addDenied(denied);
									plot.removeAllowed(denied);

									if (denied.equals("*")) {
										List<Player> deniedplayers = PlotManager.getPlayersInPlot(w, id);

										for (Player deniedplayer : deniedplayers) {
											if (!plot.isAllowed(deniedplayer.getUniqueId())) {
												deniedplayer.teleport(PlotManager.getPlotHome(w, plot));
											}
										}
									} else {
										@SuppressWarnings("deprecation")
										Player deniedplayer = Bukkit.getPlayerExact(denied);

										if (deniedplayer != null) {
											if (deniedplayer.getWorld().equals(w)) {
												String deniedid = PlotManager.getPlotId(deniedplayer);

												if (deniedid.equalsIgnoreCase(id)) {
													deniedplayer.teleport(PlotManager.getPlotHome(w, plot));
												}
											}
										}
									}

									Send(p, C("WordPlayer") + " " + RED + denied + RESET + " " + C("MsgNowDenied") + " " + f(-price));

									PlotMe.logger.info(prefixe + playername + " " + C("MsgDeniedPlayer") + " " + denied + " " + C("MsgToPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
								}
							} else {
								Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedDeny"));
							}
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean remove(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.remove") || p.hasPermission("PlotMe.use.remove")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandRemove") + " <" + C("WordPlayer") + ">");
						} else {
							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();
							UUID playeruuid = p.getUniqueId();
							String allowed = args[1];

							if (plot.ownerId.equals(playeruuid) || p.hasPermission("PlotMe.admin.remove")) {
								if (plot.isAllowedConsulting(allowed) || plot.isGroupAllowed(allowed)) {
									World w = p.getWorld();

									PlotMapInfo pmi = PlotManager.getMap(w);

									double price = 0;

									if (PlotManager.isEconomyEnabled(w)) {
										price = pmi.RemovePlayerPrice;
										double balance = PlotMe.economy.getBalance(playername);

										if (balance >= price) {
											EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

											if (!er.transactionSuccess()) {
												Send(p, RED + er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, RED + C("MsgNotEnoughRemove") + " " + C("WordMissing") + " " + RESET + f(price - balance, false));
											return true;
										}
									}

									if (allowed.startsWith("group:")) {
										plot.removeAllowedGroup(allowed);
									} else {
										plot.removeAllowed(allowed);
									}

									Send(p, C("WordPlayer") + " " + RED + allowed + RESET + " " + C("WordRemoved") + ". " + f(-price));

									PlotMe.logger.info(prefixe + p.getName() + " " + C("MsgRemovedPlayer") + " " + allowed + " " + C("MsgFromPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
								} else {
									Send(p, C("WordPlayer") + " " + RED + args[1] + RESET + " " + C("MsgWasNotAllowed"));
								}
							} else {
								Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedRemove"));
							}
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean undeny(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.undeny") || p.hasPermission("PlotMe.use.undeny")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandUndeny") + " <" + C("WordPlayer") + ">");
						} else {
							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();
							UUID playeruuid = p.getUniqueId();
							String denied = args[1];

							if (plot.ownerId.equals(playeruuid) || p.hasPermission("PlotMe.admin.undeny")) {
								if (plot.isDeniedConsulting(denied) || plot.isGroupDenied(denied)) {
									World w = p.getWorld();

									PlotMapInfo pmi = PlotManager.getMap(w);

									double price = 0;

									if (PlotManager.isEconomyEnabled(w)) {
										price = pmi.UndenyPlayerPrice;
										double balance = PlotMe.economy.getBalance(playername);

										if (balance >= price) {
											EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

											if (!er.transactionSuccess()) {
												Send(p, RED + er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, RED + C("MsgNotEnoughUndeny") + " " + C("WordMissing") + " " + RESET + f(price - balance, false));
											return true;
										}
									}

									if (denied.startsWith("group:")) {
										plot.removeDeniedGroup(denied);
									} else {
										plot.removeDenied(denied);
									}

									Send(p, C("WordPlayer") + " " + RED + denied + RESET + " " + C("MsgNowUndenied") + " " + f(-price));

									PlotMe.logger.info(prefixe + playername + " " + C("MsgUndeniedPlayer") + " " + denied + " " + C("MsgFromPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
								} else {
									Send(p, C("WordPlayer") + " " + RED + args[1] + RESET + " " + C("MsgWasNotDenied"));
								}
							} else {
								Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedUndeny"));
							}
						}
					} else {
						Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean setowner(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.setowner")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					if (args.length < 2 || args[1].isEmpty()) {
						Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandSetowner") + " <" + C("WordPlayer") + ">");
					} else {
						String newowner = args[1];
						String oldowner = "<" + C("WordNotApplicable") + ">";
						String playername = p.getName();

						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							PlotMapInfo pmi = PlotManager.getMap(p);
							oldowner = plot.owner;

							if (PlotManager.isEconomyEnabled(p)) {
								if (pmi.RefundClaimPriceOnSetOwner && !newowner.equals(oldowner)) {
									EconomyResponse er = PlotMe.economy.depositPlayer(oldowner, pmi.ClaimPrice);

									if (!er.transactionSuccess()) {
										Send(p, RED + er.errorMessage);
										warn(er.errorMessage);
										return true;
									} else {
										for (Player player : Bukkit.getServer().getOnlinePlayers()) {
											if (player.getName().equalsIgnoreCase(oldowner)) {
												Send(player, C("MsgYourPlot") + " " + id + " " + C("MsgNowOwnedBy") + " " + newowner + ". " + f(pmi.ClaimPrice));
												break;
											}
										}
									}
								}

								if (plot.currentbidder != null && !plot.currentbidder.isEmpty()) {
									EconomyResponse er = PlotMe.economy.depositPlayer(plot.currentbidder, plot.currentbid);

									if (!er.transactionSuccess()) {
										Send(p, er.errorMessage);
										warn(er.errorMessage);
									} else {
										for (Player player : Bukkit.getServer().getOnlinePlayers()) {
											if (player.getName().equalsIgnoreCase(plot.currentbidder)) {
												Send(player, C("WordPlot") + " " + id + " " + C("MsgChangedOwnerFrom") + " " + oldowner + " " + C("WordTo") + " " + newowner + ". " + f(plot.currentbid));
												break;
											}
										}
									}
								}
							}

							plot.currentbidder = "";
							plot.currentbidderId = null;
							plot.currentbid = 0;
							plot.auctionned = false;
							plot.forsale = false;

							PlotManager.setSellSign(p.getWorld(), plot);

							plot.updateField("currentbidder", "");
							plot.updateField("currentbid", 0);
							plot.updateField("auctionned", false);
							plot.updateField("forsale", false);
							plot.updateField("currentbidderid", null);

							plot.owner = newowner;

							PlotManager.setOwnerSign(p.getWorld(), plot);

							plot.updateField("owner", newowner);
						} else {
							PlotManager.createPlot(p.getWorld(), id, newowner, null);
						}

						Send(p, C("MsgOwnerChangedTo") + " " + RED + newowner);

						PlotMe.logger.info(prefixe + playername + " " + C("MsgChangedOwnerOf") + " " + id + " " + C("WordFrom") + " " + oldowner + " " + C("WordTo") + " " + newowner);
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean id(Player p) {
		if (p.hasPermission("PlotMe.admin.id")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				String plotid = PlotManager.getPlotId(p.getLocation());

				if (plotid.isEmpty()) {
					Send(p, RED + C("MsgNoPlotFound"));
				} else {
					p.sendMessage(BLUE + C("WordPlot") + " " + C("WordId") + ": " + RESET + plotid);

					Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), plotid);
					Location top = PlotManager.getPlotTopLoc(p.getWorld(), plotid);

					p.sendMessage(BLUE + C("WordBottom") + ": " + RESET + bottom.getBlockX() + ChatColor.BLUE + "," + RESET + bottom.getBlockZ());
					p.sendMessage(BLUE + C("WordTop") + ": " + RESET + top.getBlockX() + ChatColor.BLUE + "," + RESET + top.getBlockZ());
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean move(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.move")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, RED + C("MsgNotPlotWorld"));
			} else {
				if (args.length < 3 || args[1].equalsIgnoreCase("") || args[2].equalsIgnoreCase("")) {
					Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandMove") + " <" + C("WordIdFrom") + "> <" + C("WordIdTo") + "> " +
							RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandMove") + " 0;1 2;-1");
				} else {
					String plot1 = args[1];
					String plot2 = args[2];

					if (!PlotManager.isValidId(plot1) || !PlotManager.isValidId(plot2)) {
						Send(p, C("WordUsage") + ": " + RED + "/plotme " + C("CommandMove") + " <" + C("WordIdFrom") + "> <" + C("WordIdTo") + "> " +
								RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandMove") + " 0;1 2;-1");
						return true;
					} else {
						if (PlotManager.movePlot(p.getWorld(), plot1, plot2)) {
							Send(p, C("MsgPlotMovedSuccess"));

							PlotMe.logger.info(prefixe + p.getName() + " " + C("MsgExchangedPlot") + " " + plot1 + " " + C("MsgAndPlot") + " " + plot2);
						} else {
							Send(p, RED + C("ErrMovingPlot"));
						}
					}
				}
			}
		} else {
			Send(p, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean reload(CommandSender s) {
		if (!(s instanceof Player) || s.hasPermission("PlotMe.admin.reload")) {
			plugin.initialize();
			Send(s, C("MsgReloadedSuccess"));

			PlotMe.logger.info(prefixe + s.getName() + " " + C("MsgReloadedConfigurations"));
		} else {
			Send(s, RED + C("MsgPermissionDenied"));
		}
		return true;
	}

	private StringBuilder whitespace(int length) {
		int spaceWidth = MinecraftFontWidthCalculator.getStringWidth(" ");

		StringBuilder ret = new StringBuilder();

		for (int i = 0; (i + spaceWidth) < length; i += spaceWidth) {
			ret.append(" ");
		}

		return ret;
	}

	private String round(double money) {
		if (money % 1 == 0) return String.valueOf(Math.round(money));
		else return String.valueOf(money);
	}

	private void warn(String msg) {
		PlotMe.logger.warning(prefixe + msg);
	}

	private String f(double price) {
		return f(price, true);
	}

	private String f(double price, boolean showsign) {
		if (price == 0) {
			return "";
		}

		String format = round(Math.abs(price));

		if (PlotMe.economy != null) {
			format = (price <= 1 && price >= -1) ? format + " " + PlotMe.economy.currencyNameSingular() : format + " " + PlotMe.economy.currencyNamePlural();
		}

		if (showsign) {
			return GREEN + ((price > 0) ? "+" + format : "-" + format);
		} else {
			return GREEN + format;
		}
	}

	private void Send(CommandSender cs, String text) {
		cs.sendMessage(PREFIX + text);
	}

	private String FormatBiome(String biome) {
		biome = biome.toLowerCase();

		String[] tokens = biome.split("_");

		biome = "";

		for (String token : tokens) {
			token = token.substring(0, 1).toUpperCase() + token.substring(1);

			if (biome.equalsIgnoreCase("")) {
				biome = token;
			} else {
				biome = biome + "_" + token;
			}
		}

		return biome;
	}
}
