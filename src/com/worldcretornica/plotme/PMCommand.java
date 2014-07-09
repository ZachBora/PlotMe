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
	private final String PREFIX = PlotMe.PREFIX;
	private final String prefixe = "[Event] ";
	private final PlotMe plugin;

	public PMCommand(PlotMe instance) {
		plugin = instance;
	}

	public static boolean cPerms(CommandSender sender, String node) {
		return sender.hasPermission(node);
	}

	public static int getPlotLimit(Player p) {
		int max = -2;

		int maxlimit = 255;

		if (p.hasPermission("plotme.limit.*")) {
			return -1;
		} else {
			for (int ctr = 0; ctr < maxlimit; ctr++) {
				if (p.hasPermission("plotme.limit." + ctr)) {
					max = ctr;
				}
			}

		}

		if (max == -2) {
			if (cPerms(p, "plotme.admin")) {
				return -1;
			} else if (cPerms(p, "plotme.use")) {
				return 1;
			} else {
				return 0;
			}
		}

		return max;
	}

	private String C(String caption) {
		return PlotMe.caption(caption);
	}

	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
		if (l.equalsIgnoreCase("plotme")) {
			if (!(s instanceof Player)) {
				if (args.length == 0) {
					s.sendMessage(C("ConsoleHelpMain"));
					s.sendMessage(" - /plotme reload");
					s.sendMessage(C("ConsoleHelpReload"));
					return true;
				} else {
					String a0 = args[0];
					if (a0.equalsIgnoreCase("reload")) {
						return reload(s);
					}
					if (a0.equalsIgnoreCase(C("CommandResetExpired"))) {
						return resetexpired(s, args);
					}
				}
			} else {
				Player p = (Player) s;

				if (args.length == 0) {
					return showhelp(p, 1);
				} else {
					String a0 = args[0];
					int ipage = -1;

					try {
						ipage = Integer.parseInt(a0);
					} catch (Exception ignored) {
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
								} catch (Exception ignored) {
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
						if (a0.equalsIgnoreCase(C("CommandInfo"))) {
							return info(p);
						}
						if (a0.equalsIgnoreCase(C("CommandComment"))) {
							return comment(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandComments"))) {
							return comments(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandBiome"))) {
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
						if (a0.equalsIgnoreCase(C("CommandAdd"))) {
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
						if (a0.equalsIgnoreCase(C("CommandSetowner"))) {
							return setowner(p, args);
						}
						if (a0.equalsIgnoreCase(C("CommandMove"))) {
							return move(p, args);
						}
						if (a0.equalsIgnoreCase("reload")) {
							return reload(s);
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
						if (a0.startsWith(C("CommandHome"))) {
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

	private boolean resetexpired(CommandSender s, String[] args) {
		if (cPerms(s, "PlotMe.admin.resetexpired")) {
			if (args.length <= 1) {
				Send(s, C("WordUsage") + ": /plotme " + C("CommandResetExpired") + " <" + C("WordWorld") + "> Example: /plotme " + C("CommandResetExpired") + " plotworld ");
			} else {
				if (PlotMe.worldcurrentlyprocessingexpired != null) {
					Send(s, PlotMe.cscurrentlyprocessingexpired.getName() + " " + C("MsgAlreadyProcessingPlots"));
				} else {
					World w = s.getServer().getWorld(args[1]);

					if (w == null) {
						Send(s, C("WordWorld") + " '" + args[1] + "' " + C("MsgDoesNotExistOrNotLoaded"));
						return true;
					} else {
						if (!PlotManager.isPlotWorld(w)) {
							Send(s, C("MsgNotPlotWorld"));
							return true;
						} else {
							PlotMe.worldcurrentlyprocessingexpired = w;
							PlotMe.cscurrentlyprocessingexpired = s;
							PlotMe.counterexpired = 50;
							PlotMe.nbperdeletionprocessingexpired = 5;

							plugin.scheduleTask(new PlotRunnableDeleteExpire());
						}
					}
				}
			}
		} else {
			Send(s, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean bid(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			if (cPerms(p, "PlotMe.use.bid")) {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot.auctionned) {
							String bidder = p.getName();

							if (plot.owner.equalsIgnoreCase(bidder)) {
								Send(p, C("MsgCannotBidOwnPlot"));
							} else {
								if (args.length == 2) {
									double bid = 0;
									double currentbid = plot.currentbid;
									String currentbidder = plot.currentbidder;

									try {
										bid = Double.parseDouble(args[1]);
									} catch (Exception ignored) {
									}

									if (bid < currentbid || (bid == currentbid && !currentbidder.isEmpty())) {
										Send(p, C("MsgInvalidBidMustBeAbove") + " " + f(plot.currentbid, false));
									} else {
										double balance = PlotMe.economy.getBalance(bidder);

										if (bid >= balance && !currentbidder.equals(bidder) || currentbidder.equals(bidder) && bid > (balance + currentbid)) {
											Send(p, C("MsgNotEnoughBid"));
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
									Send(p, C("WordUsage") + ": /plotme " + C("CommandBid") + " <" + C("WordAmount") + "> " +
											C("WordExample") + ": /plotme " + C("CommandBid") + " 100");
								}
							}
						} else {
							Send(p, C("MsgPlotNotAuctionned"));
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			} else {
				Send(p, C("MsgPermissionDenied"));
			}
		} else {
			Send(p, C("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean buy(Player p) {
		if (PlotManager.isEconomyEnabled(p)) {
			if (cPerms(p, "PlotMe.use.buy") || cPerms(p, "PlotMe.admin.buy")) {
				Location l = p.getLocation();
				String id = PlotManager.getPlotId(l);

				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (!plot.forsale) {
							Send(p, C("MsgPlotNotForSale"));
						} else {
							String buyer = p.getName();

							if (plot.owner.equalsIgnoreCase(buyer)) {
								Send(p, C("MsgCannotBuyOwnPlot"));
							} else {
								int plotlimit = getPlotLimit(p);

								if (plotlimit != -1 && PlotManager.getNbOwnedPlot(p) >= plotlimit) {
									Send(p, C("MsgAlreadyReachedMaxPlots") + " (" +
											PlotManager.getNbOwnedPlot(p) + "/" + getPlotLimit(p) + "). " +
											C("WordUse") + " /plotme " + C("CommandHome") + " " + C("MsgToGetToIt"));
								} else {
									World w = p.getWorld();

									double cost = plot.customprice;

									if (PlotMe.economy.getBalance(buyer) < cost) {
										Send(p, C("MsgNotEnoughBuy"));
									} else {
										EconomyResponse er = PlotMe.economy.withdrawPlayer(buyer, cost);

										if (er.transactionSuccess()) {
											String oldowner = plot.owner;

											if (!oldowner.equalsIgnoreCase("$Bank$")) {
												EconomyResponse er2 = PlotMe.economy.depositPlayer(oldowner, cost);

												if (!er2.transactionSuccess()) {
													Send(p, er2.errorMessage);
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
											Send(p, er.errorMessage);
											warn(er.errorMessage);
										}
									}
								}
							}
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			} else {
				Send(p, C("MsgPermissionDenied"));
			}
		} else {
			Send(p, C("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean auction(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			PlotMapInfo pmi = PlotManager.getMap(p);

			if (pmi.CanPutOnSale) {
				if (cPerms(p, "PlotMe.use.auction") || cPerms(p, "PlotMe.admin.auction")) {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						Send(p, C("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							String name = p.getName();

							if (plot.owner.equalsIgnoreCase(name) || cPerms(p, "PlotMe.admin.auction")) {
								World w = p.getWorld();

								if (plot.auctionned) {
									if (plot.currentbidder != null && !plot.currentbidder.equalsIgnoreCase("") && !cPerms(p, "PlotMe.admin.auction")) {
										Send(p, C("MsgPlotHasBidsAskAdmin"));
									} else {
										if (plot.currentbidder != null && !plot.currentbidder.equalsIgnoreCase("")) {
											EconomyResponse er = PlotMe.economy.depositPlayer(plot.currentbidder, plot.currentbid);

											if (!er.transactionSuccess()) {
												Send(p, er.errorMessage);
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
										} catch (Exception ignored) {
										}
									}

									if (bid < 0) {
										Send(p, C("MsgInvalidAmount"));
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
								Send(p, C("MsgDoNotOwnPlot"));
							}
						} else {
							Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
						}
					}
				} else {
					Send(p, C("MsgPermissionDenied"));
				}
			} else {
				Send(p, C("MsgSellingPlotsIsDisabledWorld"));
			}
		} else {
			Send(p, C("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean dispose(Player p) {
		if (cPerms(p, "PlotMe.admin.dispose") || cPerms(p, "PlotMe.use.dispose")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot.protect) {
							Send(p, C("MsgPlotProtectedNotDisposed"));
						} else {
							String name = p.getName();

							if (plot.owner.equalsIgnoreCase(name) || cPerms(p, "PlotMe.admin.dispose")) {
								PlotMapInfo pmi = PlotManager.getMap(p);

								double cost = pmi.DisposePrice;

								if (PlotManager.isEconomyEnabled(p)) {
									if (cost != 0 && PlotMe.economy.getBalance(name) < cost) {
										Send(p, C("MsgNotEnoughDispose"));
										return true;
									}

									EconomyResponse er = PlotMe.economy.withdrawPlayer(name, cost);

									if (!er.transactionSuccess()) {
										Send(p, er.errorMessage);
										warn(er.errorMessage);
										return true;
									}

									if (plot.auctionned) {
										String currentbidder = plot.currentbidder;

										if (!currentbidder.isEmpty()) {
											EconomyResponse er2 = PlotMe.economy.depositPlayer(currentbidder, plot.currentbid);

											if (!er2.transactionSuccess()) {
												Send(p, er2.errorMessage);
												warn(er2.errorMessage);
											} else {
												for (Player player : Bukkit.getServer().getOnlinePlayers()) {
													if (player.getName().equalsIgnoreCase(currentbidder)) {
														Send(player, C("WordPlot") +
																" " + id + " " + C("MsgOwnedBy") + " " + plot.owner + " " + C("MsgWasDisposed") + " " + f(cost));
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
								Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursCannotDispose"));
							}
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean sell(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			PlotMapInfo pmi = PlotManager.getMap(p);

			if (pmi.CanSellToBank || pmi.CanPutOnSale) {
				if (cPerms(p, "PlotMe.use.sell") || cPerms(p, "PlotMe.admin.sell")) {
					Location l = p.getLocation();
					String id = PlotManager.getPlotId(l);

					if (id.isEmpty()) {
						Send(p, C("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							if (plot.owner.equalsIgnoreCase(p.getName()) || cPerms(p, "PlotMe.admin.sell")) {
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
														Send(p, C("WordUsage") + ": /plotme " + C("CommandSellBank") + "|<" + C("WordAmount") + ">");
														p.sendMessage("  " + C("WordExample") + ": /plotme " + C("CommandSellBank") + "  or  /plotme " + C("CommandSell") + " 200");
													} else {
														Send(p, C("WordUsage") + ": /plotme " + C("CommandSell") + " <" + C("WordAmount") + "> " + C("WordExample") + ": /plotme " + C("CommandSell") + " 200");
													}
												}
											} else {
												Send(p, C("MsgCannotCustomPriceDefault") + " " + price);
												return true;
											}
										}
									}

									if (bank) {
										if (!pmi.CanSellToBank) {
											Send(p, C("MsgCannotSellToBank"));
										} else {

											String currentbidder = plot.currentbidder;

											if (!currentbidder.isEmpty()) {
												double bid = plot.currentbid;

												EconomyResponse er = PlotMe.economy.depositPlayer(currentbidder, bid);

												if (!er.transactionSuccess()) {
													Send(p, er.errorMessage);
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
											Send(p, C("MsgInvalidAmount"));
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
								Send(p, C("MsgDoNotOwnPlot"));
							}
						} else {
							Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
						}
					}
				} else {
					Send(p, C("MsgPermissionDenied"));
				}
			} else {
				Send(p, C("MsgSellingPlotsIsDisabledWorld"));
			}
		} else {
			Send(p, C("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean protect(Player p) {
		if (cPerms(p, "PlotMe.admin.protect") || cPerms(p, "PlotMe.use.protect")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						String name = p.getName();

						if (plot.owner.equalsIgnoreCase(name) || cPerms(p, "PlotMe.admin.protect")) {
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
										Send(p, C("MsgNotEnoughProtectPlot"));
										return true;
									} else {
										EconomyResponse er = PlotMe.economy.withdrawPlayer(name, cost);

										if (!er.transactionSuccess()) {
											Send(p, er.errorMessage);
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
							Send(p, C("MsgDoNotOwnPlot"));
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean donelist(Player p, String[] args) {
		if (cPerms(p, "PlotMe.admin.done")) {
			PlotMapInfo pmi = PlotManager.getMap(p);

			if (pmi == null) {
				Send(p, C("MsgNotPlotWorld"));
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
					} catch (NumberFormatException ignored) {
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

						String starttext = "  " + plot.id + " -> " + plot.owner;

						int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);

						String line = starttext + whitespace(550 - textLength) + "@" + plot.finisheddate;

						p.sendMessage(line);
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean done(Player p) {
		if (cPerms(p, "PlotMe.use.done") || cPerms(p, "PlotMe.admin.done")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);
						String name = p.getName();

						if (plot.owner.equalsIgnoreCase(name) || cPerms(p, "PlotMe.admin.done")) {
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
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean addtime(Player p) {
		if (cPerms(p, "PlotMe.admin.addtime")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
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
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean expired(Player p, String[] args) {
		if (cPerms(p, "PlotMe.admin.expired")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
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
					} catch (NumberFormatException ignored) {
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

						String starttext = "  " + plot.id + " -> " + plot.owner;

						int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);

						String line = starttext + whitespace(550 - textLength) + "@" + plot.expireddate.toString();

						p.sendMessage(line);
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}


	private boolean plotlist(Player p, String[] args) {
		if (cPerms(p, "PlotMe.use.list")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
				return true;
			} else {
				String name;
				String pname = p.getName();

				if (cPerms(p, "PlotMe.admin.list") && args.length == 2) {
					name = args[1];
					Send(p, C("MsgListOfPlotsWhere") + " " + name + " " + C("MsgCanBuild"));
				} else {
					name = p.getName();
					Send(p, C("MsgListOfPlotsWhereYou"));
				}

				for (Plot plot : PlotManager.getPlots(p).values()) {
					StringBuilder addition = new StringBuilder();

					if (plot.expireddate != null) {
						java.util.Date tempdate = plot.expireddate;

						if (tempdate.compareTo(Calendar.getInstance().getTime()) < 0) {
							addition.append(" @" + plot.expireddate.toString());
						} else {
							addition.append(" @" + plot.expireddate.toString());
						}
					}

					if (plot.auctionned) {
						if (plot.currentbidder != null && !plot.currentbidder.isEmpty()) {
							addition.append(" " + C("WordAuction") + ": " + round(plot.currentbid) + (" " + plot.currentbidder));
						} else {
							addition.append(" " + C("WordAuction") + ": " + round(plot.currentbid) + "");
						}
					}

					if (plot.forsale) {
						addition.append(" " + C("WordSell") + ": " + round(plot.customprice));
					}

					if (plot.owner.equalsIgnoreCase(name)) {
						if (plot.allowedcount() == 0) {
							if (name.equalsIgnoreCase(pname)) {
								p.sendMessage("  " + plot.id + " -> " + C("WordYours") + addition);
							} else {
								p.sendMessage("  " + plot.id + " -> " + plot.owner + addition);
							}
						} else {
							StringBuilder helpers = new StringBuilder();
							for (int i = 0; i < plot.allowedcount(); i++) {
								helpers.append(plot.allowed().toArray()[i]).append(ChatColor.RESET).append(", ");
							}
							if (helpers.length() > 2) {
								helpers.delete(helpers.length() - 2, helpers.length());
							}

							if (name.equalsIgnoreCase(pname)) {
								p.sendMessage("  " + plot.id + " -> " + ChatColor.ITALIC + C("WordYours") + addition + ", " + C("WordHelpers") + ": " + helpers);
							} else {
								p.sendMessage("  " + plot.id + " -> " + ChatColor.ITALIC + plot.owner + addition + ", " + C("WordHelpers") + ": " + helpers);
							}
						}
					} else if (plot.isAllowedConsulting(name)) {
						StringBuilder helpers = new StringBuilder();
						for (int i = 0; i < plot.allowedcount(); i++) {
							if (p.getName().equalsIgnoreCase((String) plot.allowed().toArray()[i])) {
								if (name.equalsIgnoreCase(pname)) {
									helpers.append("You").append(", ");
								} else {
									helpers.append(args[1]).append(", ");
								}
							} else {
								helpers.append(plot.allowed().toArray()[i]).append(", ");
							}
						}
						if (helpers.length() > 2) {
							helpers.delete(helpers.length() - 2, helpers.length());
						}

						if (plot.owner.equalsIgnoreCase(pname)) {
							p.sendMessage("  " + plot.id + " -> " + C("WordYours") + addition + ", " + C("WordHelpers") + ": " + helpers);
						} else {
							p.sendMessage("  " + plot.id + " -> " + plot.owner + C("WordPossessive") + addition + ", " + C("WordHelpers") + ": " + helpers);
						}
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean showhelp(Player p, int page) {
		int max = 4;
		int maxpage;
		boolean ecoon = PlotManager.isEconomyEnabled(p);

		List<String> allowed_commands = new ArrayList<>();

		allowed_commands.add("limit");
		if (cPerms(p, "PlotMe.use.claim")) {
			allowed_commands.add("claim");
		}
		if (cPerms(p, "PlotMe.use.claim.other")) {
			allowed_commands.add("claim.other");
		}
		if (cPerms(p, "PlotMe.use.auto")) {
			allowed_commands.add("auto");
		}
		if (cPerms(p, "PlotMe.use.home")) {
			allowed_commands.add("home");
		}
		if (cPerms(p, "PlotMe.use.home.other")) {
			allowed_commands.add("home.other");
		}
		if (cPerms(p, "PlotMe.use.info")) {
			allowed_commands.add("info");
			allowed_commands.add("biomeinfo");
		}
		if (cPerms(p, "PlotMe.use.comment")) {
			allowed_commands.add("comment");
		}
		if (cPerms(p, "PlotMe.use.comments")) {
			allowed_commands.add("comments");
		}
		if (cPerms(p, "PlotMe.use.list")) {
			allowed_commands.add("list");
		}
		if (cPerms(p, "PlotMe.use.biome")) {
			allowed_commands.add("biome");
			allowed_commands.add("biomelist");
		}
		if (cPerms(p, "PlotMe.use.done") ||
				cPerms(p, "PlotMe.admin.done")) {
			allowed_commands.add("done");
		}
		if (cPerms(p, "PlotMe.admin.done")) {
			allowed_commands.add("donelist");
		}
		if (cPerms(p, "PlotMe.admin.tp")) {
			allowed_commands.add("tp");
		}
		if (cPerms(p, "PlotMe.admin.id")) {
			allowed_commands.add("id");
		}
		if (cPerms(p, "PlotMe.use.clear") ||
				cPerms(p, "PlotMe.admin.clear")) {
			allowed_commands.add("clear");
		}
		if (cPerms(p, "PlotMe.admin.dispose") ||
				cPerms(p, "PlotMe.use.dispose")) {
			allowed_commands.add("dispose");
		}
		if (cPerms(p, "PlotMe.admin.reset")) {
			allowed_commands.add("reset");
		}
		if (cPerms(p, "PlotMe.use.add") ||
				cPerms(p, "PlotMe.admin.add")) {
			allowed_commands.add("add");
		}
		if (cPerms(p, "PlotMe.use.remove") ||
				cPerms(p, "PlotMe.admin.remove")) {
			allowed_commands.add("remove");
		}
		if (PlotMe.allowToDeny) {
			if (cPerms(p, "PlotMe.use.deny") ||
					cPerms(p, "PlotMe.admin.deny")) {
				allowed_commands.add("deny");
			}
			if (cPerms(p, "PlotMe.use.undeny") ||
					cPerms(p, "PlotMe.admin.undeny")) {
				allowed_commands.add("undeny");
			}
		}
		if (cPerms(p, "PlotMe.admin.setowner")) {
			allowed_commands.add("setowner");
		}
		if (cPerms(p, "PlotMe.admin.move")) {
			allowed_commands.add("move");
		}
		if (cPerms(p, "PlotMe.admin.weanywhere")) {
			allowed_commands.add("weanywhere");
		}
		if (cPerms(p, "PlotMe.admin.reload")) {
			allowed_commands.add("reload");
		}
		if (cPerms(p, "PlotMe.admin.list")) {
			allowed_commands.add("listother");
		}
		if (cPerms(p, "PlotMe.admin.expired")) {
			allowed_commands.add("expired");
		}
		if (cPerms(p, "PlotMe.admin.addtime")) {
			allowed_commands.add("addtime");
		}
		if (cPerms(p, "PlotMe.admin.resetexpired")) {
			allowed_commands.add("resetexpired");
		}

		PlotMapInfo pmi = PlotManager.getMap(p);

		if (PlotManager.isPlotWorld(p) && ecoon) {
			if (cPerms(p, "PlotMe.use.buy")) {
				allowed_commands.add("buy");
			}
			if (cPerms(p, "PlotMe.use.sell")) {
				allowed_commands.add("sell");
				if (pmi.CanSellToBank) {
					allowed_commands.add("sellbank");
				}
			}
			if (cPerms(p, "PlotMe.use.auction")) {
				allowed_commands.add("auction");
			}
			if (cPerms(p, "PlotMe.use.bid")) {
				allowed_commands.add("bid");
			}
		}

		maxpage = (int) Math.ceil((double) allowed_commands.size() / max);

		if (page > maxpage) {
			page = 1;
		}

		p.sendMessage(" ---==" + C("HelpTitle") + " " + page + "/" + maxpage + "==--- ");

		for (int ctr = (page - 1) * max; ctr < (page * max) && ctr < allowed_commands.size(); ctr++) {
			String allowedcmd = allowed_commands.get(ctr);

			if (allowedcmd.equalsIgnoreCase("limit")) {
				World w;

				if (PlotManager.isPlotWorld(p)) {
					w = p.getWorld();
				} else {
					w = PlotManager.getFirstWorld();
				}

				int maxplots = getPlotLimit(p);
				int ownedplots = PlotManager.getNbOwnedPlot(p, w);

				if (maxplots == -1) {
					p.sendMessage(C("HelpYourPlotLimitWorld") + " : " + ownedplots + " " + C("HelpUsedOf") + " " + C("WordInfinite"));
				} else {
					p.sendMessage(C("HelpYourPlotLimitWorld") + " : " + ownedplots + " " + C("HelpUsedOf") + " " + maxplots);
				}
			} else if (allowedcmd.equalsIgnoreCase("claim")) {
				p.sendMessage(" /plotme " + C("CommandClaim"));
				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					p.sendMessage(" " + C("HelpClaim") + " " + C("WordPrice") + " : " + round(pmi.ClaimPrice));
				} else {
					p.sendMessage(" " + C("HelpClaim"));
				}
			} else if (allowedcmd.equalsIgnoreCase("claim.other")) {
				p.sendMessage(" /plotme " + C("CommandClaim") + " <" + C("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					p.sendMessage(" " + C("HelpClaimOther") + " " + C("WordPrice") + " : " + round(pmi.ClaimPrice));
				} else {
					p.sendMessage(" " + C("HelpClaimOther"));
				}
			} else if (allowedcmd.equalsIgnoreCase("auto")) {
				p.sendMessage(" /plotme " + C("CommandAuto") + " [" + C("WordWorld") + "]");

				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					p.sendMessage(" " + C("HelpAuto") + " " + C("WordPrice") + " : " + round(pmi.ClaimPrice));
				} else {
					p.sendMessage(" " + C("HelpAuto"));
				}
			} else if (allowedcmd.equalsIgnoreCase("home")) {
				p.sendMessage(" /plotme " + C("CommandHome") + "[:#] [" + C("WordWorld") + "]");

				if (ecoon && pmi != null && pmi.PlotHomePrice != 0) {
					p.sendMessage(" " + C("HelpHome") + " " + C("WordPrice") + " : " + round(pmi.PlotHomePrice));
				} else {
					p.sendMessage(" " + C("HelpHome"));
				}
			} else if (allowedcmd.equalsIgnoreCase("home.other")) {
				p.sendMessage(" /plotme " + C("CommandHome") + "[:#] <" + C("WordPlayer") + "> [" + C("WordWorld") + "]");

				if (ecoon && pmi != null && pmi.PlotHomePrice != 0) {
					p.sendMessage(" " + C("HelpHomeOther") + " " + C("WordPrice") + " : " + round(pmi.PlotHomePrice));
				} else {
					p.sendMessage(" " + C("HelpHomeOther"));
				}
			} else if (allowedcmd.equalsIgnoreCase("info")) {
				p.sendMessage(" /plotme " + C("CommandInfo"));
				p.sendMessage(" " + C("HelpInfo"));
			} else if (allowedcmd.equalsIgnoreCase("comment")) {
				p.sendMessage(" /plotme " + C("CommandComment") + " <" + C("WordComment") + ">");
				if (ecoon && pmi != null && pmi.AddCommentPrice != 0) {
					p.sendMessage(" " + C("HelpComment") + " " + C("WordPrice") + " : " + round(pmi.AddCommentPrice));
				} else {
					p.sendMessage(" " + C("HelpComment"));
				}
			} else if (allowedcmd.equalsIgnoreCase("comments")) {
				p.sendMessage(" /plotme " + C("CommandComments"));
				p.sendMessage(" " + C("HelpComments"));
			} else if (allowedcmd.equalsIgnoreCase("list")) {
				p.sendMessage(" /plotme " + C("CommandList"));
				p.sendMessage(" " + C("HelpList"));
			} else if (allowedcmd.equalsIgnoreCase("listother")) {
				p.sendMessage(" /plotme " + C("CommandList") + " <" + C("WordPlayer") + ">");
				p.sendMessage(" " + C("HelpListOther"));
			} else if (allowedcmd.equalsIgnoreCase("biomeinfo")) {
				p.sendMessage(" /plotme " + C("CommandBiome"));
				p.sendMessage(" " + C("HelpBiomeInfo"));
			} else if (allowedcmd.equalsIgnoreCase("biome")) {
				p.sendMessage(" /plotme " + C("CommandBiome") + " <" + C("WordBiome") + ">");
				if (ecoon && pmi != null && pmi.BiomeChangePrice != 0) {
					p.sendMessage(" " + C("HelpBiome") + " " + C("WordPrice") + " : " + round(pmi.BiomeChangePrice));
				} else {
					p.sendMessage(" " + C("HelpBiome"));
				}
			} else if (allowedcmd.equalsIgnoreCase("biomelist")) {
				p.sendMessage(" /plotme " + C("CommandBiomelist"));
				p.sendMessage(" " + C("HelpBiomeList"));
			} else if (allowedcmd.equalsIgnoreCase("done")) {
				p.sendMessage(" /plotme " + C("CommandDone"));
				p.sendMessage(" " + C("HelpDone"));
			} else if (allowedcmd.equalsIgnoreCase("tp")) {
				p.sendMessage(" /plotme " + C("CommandTp") + " <" + C("WordId") + "> [" + C("WordWorld") + "]");

				p.sendMessage(" " + C("HelpTp"));
			} else if (allowedcmd.equalsIgnoreCase("id")) {
				p.sendMessage(" /plotme " + C("CommandId"));
				p.sendMessage(" " + C("HelpId"));
			} else if (allowedcmd.equalsIgnoreCase("clear")) {
				p.sendMessage(" /plotme " + C("CommandClear"));
				if (ecoon && pmi != null && pmi.ClearPrice != 0) {
					p.sendMessage(" " + C("HelpId") + " " + C("WordPrice") + " : " + round(pmi.ClearPrice));
				} else {
					p.sendMessage(" " + C("HelpClear"));
				}
			} else if (allowedcmd.equalsIgnoreCase("reset")) {
				p.sendMessage(" /plotme " + C("CommandReset"));
				p.sendMessage(" " + C("HelpReset"));
			} else if (allowedcmd.equalsIgnoreCase("add")) {
				p.sendMessage(" /plotme " + C("CommandAdd") + " <" + C("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.AddPlayerPrice != 0) {
					p.sendMessage(" " + C("HelpAdd") + " " + C("WordPrice") + " : " + round(pmi.AddPlayerPrice));
				} else {
					p.sendMessage(" " + C("HelpAdd"));
				}
			} else if (allowedcmd.equalsIgnoreCase("deny")) {
				p.sendMessage(" /plotme " + C("CommandDeny") + " <" + C("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.DenyPlayerPrice != 0) {
					p.sendMessage(" " + C("HelpDeny") + " " + C("WordPrice") + " : " + round(pmi.DenyPlayerPrice));
				} else {
					p.sendMessage(" " + C("HelpDeny"));
				}
			} else if (allowedcmd.equalsIgnoreCase("remove")) {
				p.sendMessage(" /plotme " + C("CommandRemove") + " <" + C("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.RemovePlayerPrice != 0) {
					p.sendMessage(" " + C("HelpRemove") + " " + C("WordPrice") + " : " + round(pmi.RemovePlayerPrice));
				} else {
					p.sendMessage(" " + C("HelpRemove"));
				}
			} else if (allowedcmd.equalsIgnoreCase("undeny")) {
				p.sendMessage(" /plotme " + C("CommandUndeny") + " <" + C("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.UndenyPlayerPrice != 0) {
					p.sendMessage(" " + C("HelpUndeny") + " " + C("WordPrice") + " : " + round(pmi.UndenyPlayerPrice));
				} else {
					p.sendMessage(" " + C("HelpUndeny"));
				}
			} else if (allowedcmd.equalsIgnoreCase("setowner")) {
				p.sendMessage(" /plotme " + C("CommandSetowner") + " <" + C("WordPlayer") + ">");
				p.sendMessage(" " + C("HelpSetowner"));
			} else if (allowedcmd.equalsIgnoreCase("move")) {
				p.sendMessage(" /plotme " + C("CommandMove") + " <" + C("WordIdFrom") + "> <" + C("WordIdTo") + ">");
				p.sendMessage(" " + C("HelpMove"));
			} else if (allowedcmd.equalsIgnoreCase("weanywhere")) {
				p.sendMessage(" /plotme " + C("CommandWEAnywhere"));
				p.sendMessage(" " + C("HelpWEAnywhere"));
			} else if (allowedcmd.equalsIgnoreCase("expired")) {
				p.sendMessage(" /plotme " + C("CommandExpired") + " [page]");
				p.sendMessage(" " + C("HelpExpired"));
			} else if (allowedcmd.equalsIgnoreCase("donelist")) {
				p.sendMessage(" /plotme " + C("CommandDoneList") + " [page]");
				p.sendMessage(" " + C("HelpDoneList"));
			} else if (allowedcmd.equalsIgnoreCase("addtime")) {
				p.sendMessage(" /plotme " + C("CommandAddtime"));
				int days = (pmi == null) ? 0 : pmi.DaysToExpiration;
				if (days == 0) {
					p.sendMessage(" " + C("HelpAddTime1") + " " + C("WordNever"));
				} else {
					p.sendMessage(" " + C("HelpAddTime1") + " " + days + " " + C("HelpAddTime2"));
				}
			} else if (allowedcmd.equalsIgnoreCase("reload")) {
				p.sendMessage(" /plotme reload");
				p.sendMessage(" " + C("HelpReload"));
			} else if (allowedcmd.equalsIgnoreCase("dispose")) {
				p.sendMessage(" /plotme " + C("CommandDispose"));
				if (ecoon && pmi != null && pmi.DisposePrice != 0) {
					p.sendMessage(" " + C("HelpDispose") + " " + C("WordPrice") + " : " + round(pmi.DisposePrice));
				} else {
					p.sendMessage(" " + C("HelpDispose"));
				}
			} else if (allowedcmd.equalsIgnoreCase("buy")) {
				p.sendMessage(" /plotme " + C("CommandBuy"));
				p.sendMessage(" " + C("HelpBuy"));
			} else if (allowedcmd.equalsIgnoreCase("sell")) {
				p.sendMessage(" /plotme " + C("CommandSell") + " [" + C("WordAmount") + "]");
				p.sendMessage(" " + C("HelpSell") + " " + C("WordDefault") + " : " + round(pmi.SellToPlayerPrice));
			} else if (allowedcmd.equalsIgnoreCase("sellbank")) {
				p.sendMessage(" /plotme " + C("CommandSellBank"));
				p.sendMessage(" " + C("HelpSellBank") + " " + round(pmi.SellToBankPrice));
			} else if (allowedcmd.equalsIgnoreCase("auction")) {
				p.sendMessage(" /plotme " + C("CommandAuction") + " [" + C("WordAmount") + "]");
				p.sendMessage(" " + C("HelpAuction") + " " + C("WordDefault") + " : " + "1");
			} else if (allowedcmd.equalsIgnoreCase("resetexpired")) {
				p.sendMessage(" /plotme " + C("CommandResetExpired") + " <" + C("WordWorld") + ">");
				p.sendMessage(" " + C("HelpResetExpired"));
			} else if (allowedcmd.equalsIgnoreCase("bid")) {
				p.sendMessage(" /plotme " + C("CommandBid") + " <" + C("WordAmount") + ">");
				p.sendMessage(" " + C("HelpBid"));
			}
		}

		return true;
	}

	private boolean tp(Player p, String[] args) {
		if (cPerms(p, "PlotMe.admin.tp")) {
			if (args.length == 2 || (args.length == 3)) {
				String id = args[1];

				if (!PlotManager.isValidId(id)) {
					Send(p, C("WordUsage") + ": /plotme " + C("CommandTp") + " <" + C("WordId") + "> [" + C("WordWorld") + "] " + C("WordExample") + ": /plotme " + C("CommandTp") + " 5;-1 ");
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
						Send(p, C("MsgNoPlotworldFound"));
					} else {
						Location bottom = PlotManager.getPlotBottomLoc(w, id);
						Location top = PlotManager.getPlotTopLoc(w, id);

						p.teleport(new Location(w, bottom.getX() + (top.getBlockX() - bottom.getBlockX()) / 2, PlotManager.getMap(w).RoadHeight + 2, bottom.getZ() - 2));
					}
				}
			} else {
				Send(p, C("WordUsage") + ": /plotme " + C("CommandTp") + " <" + C("WordId") + "> [" + C("WordWorld") + "] " + C("WordExample") + ": /plotme " + C("CommandTp") + " 5;-1 ");
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean auto(Player p, String[] args) {
		if (cPerms(p, "PlotMe.use.auto")) {
			World w;

			if (!PlotManager.isPlotWorld(p)) {
				if (args.length == 2) {
					w = Bukkit.getWorld(args[1]);
				} else {
					w = PlotManager.getFirstWorld();
				}

				if (w == null || !PlotManager.isPlotWorld(w)) {
					Send(p, args[1] + " " + C("MsgWorldNotPlot"));
					return true;
				}
			} else {
				w = p.getWorld();
			}

			if (w == null) {
				Send(p, C("MsgNoPlotworldFound"));
			} else {
				if (PlotManager.getNbOwnedPlot(p, w) >= getPlotLimit(p) && !cPerms(p, "PlotMe.admin")) {
					Send(p, C("MsgAlreadyReachedMaxPlots") + " (" +
							PlotManager.getNbOwnedPlot(p, w) + "/" + getPlotLimit(p) + "). " + C("WordUse") + " " + "/plotme " + C("CommandHome") + " " + C("MsgToGetToIt"));
				} else {
					PlotMapInfo pmi = PlotManager.getMap(w);
					int limit = pmi.PlotAutoLimit;

					for (int i = 0; i < limit; i++) {
						for (int x = -i; x <= i; x++) {
							for (int z = -i; z <= i; z++) {
								String id = "" + x + ";" + z;

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
												Send(p, er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, C("MsgNotEnoughAuto") + " " + C("WordMissing") + " " + f(price - balance, false));
											return true;
										}
									}

									Plot plot = PlotManager.createPlot(w, id, name, uuid);

									//PlotManager.adjustLinkedPlots(id, w);

									p.teleport(new Location(w, PlotManager.bottomX(plot.id, w) + (PlotManager.topX(plot.id, w) -
											PlotManager.bottomX(plot.id, w)) / 2, pmi.RoadHeight + 2, PlotManager.bottomZ(plot.id, w) - 2));

									Send(p, C("MsgThisPlotYours") + " " + C("WordUse") + " /plotme " + C("CommandHome") + " " + C("MsgToGetToIt") + " " + f(-price));

									PlotMe.logger.info(prefixe + name + " " + C("MsgClaimedPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));

									return true;
								}
							}
						}
					}

					Send(p, C("MsgNoPlotFound1") + " " + (limit ^ 2) + " " + C("MsgNoPlotFound2"));
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean claim(Player p, String[] args) {
		if (cPerms(p, "PlotMe.use.claim") || cPerms(p, "PlotMe.admin.claim.other")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, C("MsgCannotClaimRoad"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					Send(p, C("MsgThisPlotOwned"));
				} else {
					String playername = p.getName();
					UUID uuid = p.getUniqueId();

					if (args.length == 2) {
						if (cPerms(p, "PlotMe.admin.claim.other")) {
							playername = args[1];
							uuid = null;
						}
					}

					int plotlimit = getPlotLimit(p);

					if (playername.equals(p.getName()) && plotlimit != -1 && PlotManager.getNbOwnedPlot(p) >= plotlimit) {
						Send(p, C("MsgAlreadyReachedMaxPlots") + " (" + PlotManager.getNbOwnedPlot(p) + "/" + getPlotLimit(p) + "). " + C("WordUse") + " /plotme " + C("CommandHome") + " " + C("MsgToGetToIt"));
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
									Send(p, er.errorMessage);
									warn(er.errorMessage);
									return true;
								}
							} else {
								Send(p, C("MsgNotEnoughBuy") + " " + C("WordMissing") + " " + (price - balance) + " " + PlotMe.economy.currencyNamePlural());
								return true;
							}
						}

						Plot plot = PlotManager.createPlot(w, id, playername, uuid);

						//PlotManager.adjustLinkedPlots(id, w);

						if (plot == null) {
							Send(p, C("ErrCreatingPlotAt") + " " + id);
						} else {
							if (playername.equalsIgnoreCase(p.getName())) {
								Send(p, C("MsgThisPlotYours") + " " + C("WordUse") + " /plotme " + C("CommandHome") + " " + C("MsgToGetToIt") + " " + f(-price));
							} else {
								Send(p, C("MsgThisPlotIsNow") + " " + playername + C("WordPossessive") + ". " + C("WordUse") + " /plotme " + C("CommandHome") + " " + C("MsgToGetToIt") + " " + f(-price));
							}

							PlotMe.logger.info(prefixe + playername + " " + C("MsgClaimedPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
						}
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean home(Player p, String[] args) {
		if (cPerms(p, "PlotMe.use.home") || cPerms(p, "PlotMe.admin.home.other")) {
			String playername = p.getName();
			UUID uuid = p.getUniqueId();
			int nb = 1;
			World w;
			String worldname = "";

			if (!PlotManager.isPlotWorld(p)) {
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
						Send(p, C("WordUsage") + ": /plotme " + C("CommandHome") + ":# " + C("WordExample") + ": /plotme " + C("CommandHome") + ":1");
						return true;
					} else {
						nb = Integer.parseInt(args[0].split(":")[1]);
					}
				} catch (Exception ex) {
					Send(p, C("WordUsage") + ": /plotme " + C("CommandHome") + ":# " + C("WordExample") + ": /plotme " + C("CommandHome") + ":1");
					return true;
				}
			}

			if (args.length == 2) {
				if (Bukkit.getWorld(args[1]) == null) {
					if (cPerms(p, "PlotMe.admin.home.other")) {
						playername = args[1];
						uuid = null;
					}
				} else {
					w = Bukkit.getWorld(args[1]);
					worldname = args[1];
				}
			}

			if (!PlotManager.isPlotWorld(w)) {
				Send(p, worldname + " " + C("MsgWorldNotPlot"));
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
										Send(p, er.errorMessage);
										return true;
									}
								} else {
									Send(p, C("MsgNotEnoughTp") + " " + C("WordMissing") + " " + f(price - balance, false));
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
						Send(p, playername + " " + C("MsgDoesNotHavePlot") + " #" + nb);
					} else {
						Send(p, C("MsgPlotNotFound") + " #" + nb);
					}
				} else if (!playername.equalsIgnoreCase(p.getName())) {
					Send(p, playername + " " + C("MsgDoesNotHavePlot"));
				} else {
					Send(p, C("MsgYouHaveNoPlot"));
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean info(Player p) {
		if (cPerms(p, "PlotMe.use.info")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						p.sendMessage(C("InfoId") + ": " + id +
								" " + C("InfoOwner") + ": " + plot.owner +
								" " + C("InfoBiome") + ": " + FormatBiome(plot.biome.name()));

						p.sendMessage(C("InfoExpire") + ": " + ((plot.expireddate == null) ? C("WordNever") : plot.expireddate.toString()) +
								" " + C("InfoFinished") + ": " + ((plot.finished) ? C("WordYes") : C("WordNo")) +
								" " + C("InfoProtected") + ": " + ((plot.protect) ? C("WordYes") : C("WordNo")));

						if (plot.allowedcount() > 0) {
							p.sendMessage(C("InfoHelpers") + ": " + plot.getAllowed());
						}

						if (PlotMe.allowToDeny && plot.deniedcount() > 0) {
							p.sendMessage(C("InfoDenied") + ": " + plot.getDenied());
						}

						if (PlotManager.isEconomyEnabled(p)) {
							if (plot.currentbidder == null || plot.currentbidder.equalsIgnoreCase("")) {
								p.sendMessage(C("InfoAuctionned") + ": " + ((plot.auctionned) ? C("WordYes") +
										" " + C("InfoMinimumBid") + ": " + round(plot.currentbid) : C("WordNo")) +
										" " + C("InfoForSale") + ": " + ((plot.forsale) ? round(plot.customprice) : C("WordNo")));
							} else {
								if (plot.auctionned) {
									if (plot.forsale) {
										p.sendMessage(C("InfoAuctionned") + ": " + (C("WordYes") +
												" " + C("InfoBidder") + ": " + plot.currentbidder + " " + C("InfoBid") + ": " + round(plot.currentbid)) +
												" " + C("InfoForSale") + ": " + round(plot.customprice));
									} else {
										p.sendMessage(C("InfoAuctionned") + ": " + (C("WordYes") + " " + C("InfoBidder") + ": " + plot.currentbidder + " " + C("InfoBid") + ": " + round(plot.currentbid)) +
												" " + C("InfoForSale") + ": " + C("WordNo"));
									}
								} else {
									if (plot.forsale) {
										p.sendMessage(C("InfoAuctionned") + ": " + C("WordNo") + " " + C("InfoForSale") + ": " + round(plot.customprice));
									} else {
										p.sendMessage(C("InfoAuctionned") + ": " + C("WordNo") + " " + C("InfoForSale") + ": " + C("WordNo"));
									}
								}
							}
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean comment(Player p, String[] args) {
		if (cPerms(p, "PlotMe.use.comment")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				if (args.length < 2) {
					Send(p, C("WordUsage") + ": /plotme " + C("CommandComment") + " <" + C("WordText") + ">");
				} else {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						Send(p, C("MsgNoPlotFound"));
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
										Send(p, er.errorMessage);
										warn(er.errorMessage);
										return true;
									}
								} else {
									Send(p, C("MsgNotEnoughComment") + " " + C("WordMissing") + " " + f(price - balance, false));
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
							Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
						}
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean comments(Player p, String[] args) {
		if (cPerms(p, "PlotMe.use.comments")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				if (args.length < 2) {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						Send(p, C("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							if (plot.ownerId.equals(p.getUniqueId()) || plot.isAllowed(p.getUniqueId()) || cPerms(p, "PlotMe.admin")) {
								if (plot.comments.size() == 0) {
									Send(p, C("MsgNoComments"));
								} else {
									Send(p, C("MsgYouHave") + " " +	plot.comments.size() + " " + C("MsgComments"));

									for (String[] comment : plot.comments) {
										p.sendMessage(C("WordFrom") + " : " + comment[0]);
										p.sendMessage("" + comment[1]);
									}

								}
							} else {
								Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedViewComments"));
							}
						} else {
							Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
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
		if (cPerms(p, "PlotMe.use.biome")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
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
								Send(p, args[1] + " " + C("MsgIsInvalidBiome"));
							} else {
								Plot plot = PlotManager.getPlotById(p, id);
								String playername = p.getName();

								if (plot.owner.equalsIgnoreCase(playername) || cPerms(p, "PlotMe.admin")) {
									PlotMapInfo pmi = PlotManager.getMap(w);

									double price = 0;

									if (PlotManager.isEconomyEnabled(w)) {
										price = pmi.BiomeChangePrice;
										double balance = PlotMe.economy.getBalance(playername);

										if (balance >= price) {
											EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

											if (!er.transactionSuccess()) {
												Send(p, er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, C("MsgNotEnoughBiome") + " " + C("WordMissing") + " " + f(price - balance, false));
											return true;
										}
									}

									PlotManager.setBiome(w, id, plot, biome);

									Send(p, C("MsgBiomeSet") + " " + FormatBiome(biome.name()) + " " + f(-price));

									PlotMe.logger.info(prefixe + playername + " " + C("MsgChangedBiome") + " " + id + " " + C("WordTo") + " " +
											FormatBiome(biome.name()) + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
								} else {
									Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedBiome"));
								}
							}
						} else {
							Plot plot = PlotMe.plotmaps.get(w.getName().toLowerCase()).plots.get(id);

							Send(p, C("MsgPlotUsingBiome") + " " + FormatBiome(plot.biome.name()));
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean biomelist(CommandSender s) {
		if (!(s instanceof Player) || cPerms(s, "PlotMe.use.biome")) {
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

				s.sendMessage("" + line);
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
			Send(s, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean reset(Player p) {
		if (cPerms(p, "PlotMe.admin.reset")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				Plot plot = PlotManager.getPlotById(p.getLocation());

				if (plot == null) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (plot.protect) {
						Send(p, C("MsgPlotProtectedCannotReset"));
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
									Send(p, er.errorMessage);
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
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean clear(Player p) {
		if (cPerms(p, "PlotMe.admin.clear") || cPerms(p, "PlotMe.use.clear")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot.protect) {
							Send(p, C("MsgPlotProtectedCannotClear"));
						} else {
							String playername = p.getName();

							if (plot.owner.equalsIgnoreCase(playername) || cPerms(p, "PlotMe.admin.clear")) {
								World w = p.getWorld();

								PlotMapInfo pmi = PlotManager.getMap(w);

								double price = 0;

								if (PlotManager.isEconomyEnabled(w)) {
									price = pmi.ClearPrice;
									double balance = PlotMe.economy.getBalance(playername);

									if (balance >= price) {
										EconomyResponse er = PlotMe.economy.withdrawPlayer(playername, price);

										if (!er.transactionSuccess()) {
											Send(p, er.errorMessage);
											warn(er.errorMessage);
											return true;
										}
									} else {
										Send(p, C("MsgNotEnoughClear") + " " + C("WordMissing") + " " + (price - balance) + " " + PlotMe.economy.currencyNamePlural());
										return true;
									}
								}

								PlotManager.clear(w, plot);
								//RemoveLWC(w, plot, p);
								//PlotManager.regen(w, plot);

								Send(p, C("MsgPlotCleared") + " " + f(-price));

								PlotMe.logger.info(prefixe + playername + " " + C("MsgClearedPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
							} else {
								Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedClear"));
							}
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean add(Player p, String[] args) {
		if (cPerms(p, "PlotMe.admin.add") || cPerms(p, "PlotMe.use.add")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							Send(p, C("WordUsage") + " /plotme " + C("CommandAdd") + " <" + C("WordPlayer") + ">");
						} else {

							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();
							String allowed = args[1];

							if (plot.owner.equalsIgnoreCase(playername) || cPerms(p, "PlotMe.admin.add")) {
								if (plot.isAllowedConsulting(allowed) || plot.isGroupAllowed(allowed)) {
									Send(p, C("WordPlayer") + " " + args[1] + " " + C("MsgAlreadyAllowed"));
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
												Send(p, er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, C("MsgNotEnoughAdd") + " " + C("WordMissing") + " " + f(price - balance, false));
											return true;
										}
									}

									plot.addAllowed(allowed);
									plot.removeDenied(allowed);

									Send(p, C("WordPlayer") + " " + allowed + " " + C("MsgNowAllowed") + " " + f(-price));

									PlotMe.logger.info(prefixe + playername + " " + C("MsgAddedPlayer") + " " + allowed + " " + C("MsgToPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
								}
							} else {
								Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedAdd"));
							}
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean deny(Player p, String[] args) {
		if (cPerms(p, "PlotMe.admin.deny") || cPerms(p, "PlotMe.use.deny")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							Send(p, C("WordUsage") + " /plotme " + C("CommandDeny") + " <" + C("WordPlayer") + ">");
						} else {

							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();
							String denied = args[1];

							if (plot.owner.equalsIgnoreCase(playername) || cPerms(p, "PlotMe.admin.deny")) {
								if (plot.isDeniedConsulting(denied) || plot.isGroupDenied(denied)) {
									Send(p, C("WordPlayer") + " " + args[1] + " " + C("MsgAlreadyDenied"));
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
												Send(p, er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, C("MsgNotEnoughDeny") + " " + C("WordMissing") + " " + f(price - balance, false));
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

									Send(p, C("WordPlayer") + " " + denied + " " + C("MsgNowDenied") + " " + f(-price));

									PlotMe.logger.info(prefixe + playername + " " + C("MsgDeniedPlayer") + " " + denied + " " + C("MsgToPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
								}
							} else {
								Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedDeny"));
							}
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean remove(Player p, String[] args) {
		if (cPerms(p, "PlotMe.admin.remove") || cPerms(p, "PlotMe.use.remove")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							Send(p, C("WordUsage") + ": /plotme " + C("CommandRemove") + " <" + C("WordPlayer") + ">");
						} else {
							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();
							UUID playeruuid = p.getUniqueId();
							String allowed = args[1];

							if (plot.ownerId.equals(playeruuid) || cPerms(p, "PlotMe.admin.remove")) {
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
												Send(p, er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, C("MsgNotEnoughRemove") + " " + C("WordMissing") + " " + f(price - balance, false));
											return true;
										}
									}

									if (allowed.startsWith("group:")) {
										plot.removeAllowedGroup(allowed);
									} else {
										plot.removeAllowed(allowed);
									}

									Send(p, C("WordPlayer") + " " + allowed + " " + C("WordRemoved") + ". " + f(-price));

									PlotMe.logger.info(prefixe + p.getName() + " " + C("MsgRemovedPlayer") + " " + allowed + " " + C("MsgFromPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
								} else {
									Send(p, C("WordPlayer") + " " + args[1] + " " + C("MsgWasNotAllowed"));
								}
							} else {
								Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedRemove"));
							}
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean undeny(Player p, String[] args) {
		if (cPerms(p, "PlotMe.admin.undeny") || cPerms(p, "PlotMe.use.undeny")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							Send(p, C("WordUsage") + ": /plotme " + C("CommandUndeny") + " <" + C("WordPlayer") + ">");
						} else {
							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();
							UUID playeruuid = p.getUniqueId();
							String denied = args[1];

							if (plot.ownerId.equals(playeruuid) || cPerms(p, "PlotMe.admin.undeny")) {
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
												Send(p, er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, C("MsgNotEnoughUndeny") + " " + C("WordMissing") + " " + f(price - balance, false));
											return true;
										}
									}

									if (denied.startsWith("group:")) {
										plot.removeDeniedGroup(denied);
									} else {
										plot.removeDenied(denied);
									}

									Send(p, C("WordPlayer") + " " + denied + " " + C("MsgNowUndenied") + " " + f(-price));

									PlotMe.logger.info(prefixe + playername + " " + C("MsgUndeniedPlayer") + " " + denied + " " + C("MsgFromPlot") + " " + id + ((price != 0) ? " " + C("WordFor") + " " + price : ""));
								} else {
									Send(p, C("WordPlayer") + " " + args[1] + " " + C("MsgWasNotDenied"));
								}
							} else {
								Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedUndeny"));
							}
						}
					} else {
						Send(p, C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean setowner(Player p, String[] args) {
		if (cPerms(p, "PlotMe.admin.setowner")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					if (args.length < 2 || args[1].isEmpty()) {
						Send(p, C("WordUsage") + ": /plotme " + C("CommandSetowner") + " <" + C("WordPlayer") + ">");
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
										Send(p, er.errorMessage);
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

						Send(p, C("MsgOwnerChangedTo") + " " + newowner);

						PlotMe.logger.info(prefixe + playername + " " + C("MsgChangedOwnerOf") + " " + id + " " + C("WordFrom") + " " + oldowner + " " + C("WordTo") + " " + newowner);
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean id(Player p) {
		if (cPerms(p, "PlotMe.admin.id")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				String plotid = PlotManager.getPlotId(p.getLocation());

				if (plotid.isEmpty()) {
					Send(p, C("MsgNoPlotFound"));
				} else {
					p.sendMessage(C("WordPlot") + " " + C("WordId") + ": " + plotid);

					Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), plotid);
					Location top = PlotManager.getPlotTopLoc(p.getWorld(), plotid);

					p.sendMessage(C("WordBottom") + ": " + bottom.getBlockX() + "," + bottom.getBlockZ());
					p.sendMessage(C("WordTop") + ": " + top.getBlockX() + "," + top.getBlockZ());
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean move(Player p, String[] args) {
		if (cPerms(p, "PlotMe.admin.move")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else {
				if (args.length < 3 || args[1].equalsIgnoreCase("") || args[2].equalsIgnoreCase("")) {
					Send(p, C("WordUsage") + ": /plotme " + C("CommandMove") + " <" + C("WordIdFrom") + "> <" + C("WordIdTo") + "> " + C("WordExample") + ": /plotme " + C("CommandMove") + " 0;1 2;-1");
				} else {
					String plot1 = args[1];
					String plot2 = args[2];

					if (!PlotManager.isValidId(plot1) || !PlotManager.isValidId(plot2)) {
						Send(p, C("WordUsage") + ": /plotme " + C("CommandMove") + " <" + C("WordIdFrom") + "> <" + C("WordIdTo") + "> " +
								C("WordExample") + ": /plotme " + C("CommandMove") + " 0;1 2;-1");
						return true;
					} else {
						if (PlotManager.movePlot(p.getWorld(), plot1, plot2)) {
							Send(p, C("MsgPlotMovedSuccess"));

							PlotMe.logger.info(prefixe + p.getName() + " " + C("MsgExchangedPlot") + " " + plot1 + " " + C("MsgAndPlot") + " " + plot2);
						} else {
							Send(p, C("ErrMovingPlot"));
						}
					}
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean reload(CommandSender s) {
		if (!(s instanceof Player) || cPerms(s, "PlotMe.admin.reload")) {
			plugin.initialize();
			Send(s, C("MsgReloadedSuccess"));

			PlotMe.logger.info(prefixe + s.getName() + " " + C("MsgReloadedConfigurations"));
		} else {
			Send(s, C("MsgPermissionDenied"));
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
		return (money % 1 == 0) ? "" + Math.round(money) : "" + money;
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
			return ((price > 0) ? "+" + format : "-" + format);
		} else {
			return format;
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
