package com.worldcretornica.plotme;

import com.worldcretornica.plotme.utils.MinecraftFontWidthCalculator;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static com.worldcretornica.plotme.PlotMe.*;

public class PMCommand implements CommandExecutor {
	private final String prefixe = "[Event] ";
	private final PlotMe plugin;

	public PMCommand(PlotMe instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("plotme") || label.equalsIgnoreCase("plot") || label.equalsIgnoreCase("p")) {
			if (!(sender instanceof Player)) {
				if (args.length == 0) {
					sender.sendMessage(caption("ConsoleHelpMain"));
					sender.sendMessage(" - /plotme reload");
					sender.sendMessage(caption("ConsoleHelpReload"));
					return true;
				}
				String a0 = args[0];
				if (a0.equalsIgnoreCase("reload")) {
					return reload(sender);
				}
				if (a0.equalsIgnoreCase(caption("CommandResetExpired"))) {
					return resetexpired(sender, args);
				}
			} else {
				Player p = (Player) sender;

				if (args.length == 0) {
					return showhelp(p, 1);
				}
				String a0 = args[0];
				int ipage = -1;

				try {
					ipage = Integer.parseInt(a0);
				} catch (NumberFormatException ignored) {
				}

				if (ipage != -1) {
					return showhelp(p, ipage);
				}
				if (a0.equalsIgnoreCase(caption("CommandHelp"))) {
					ipage = -1;

					if (args.length > 1) {
						String a1 = args[1];
						ipage = -1;

						try {
							ipage = Integer.parseInt(a1);
						} catch (NumberFormatException ignored) {
						}
					}

					if (ipage != -1) {
						return showhelp(p, ipage);
					}
					return showhelp(p, 1);
				}
				if (a0.equalsIgnoreCase(caption("CommandClaim"))) {
					return claim(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandAuto"))) {
					return auto(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandInfo")) || a0.equalsIgnoreCase("i")) {
					return info(p);
				}
				if (a0.equalsIgnoreCase(caption("CommandComment"))) {
					return comment(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandComments")) || a0.equalsIgnoreCase("c")) {
					return comments(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandBiome")) || a0.equalsIgnoreCase("b")) {
					return biome(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandBiomelist"))) {
					return biomelist(p);
				}
				if (a0.equalsIgnoreCase(caption("CommandId"))) {
					return id(p);
				}
				if (a0.equalsIgnoreCase(caption("CommandTp"))) {
					return tp(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandClear"))) {
					return clear(p);
				}
				if (a0.equalsIgnoreCase(caption("CommandReset"))) {
					return reset(p);
				}
				if (a0.equalsIgnoreCase(caption("CommandAdd")) || a0.equalsIgnoreCase("+")) {
					return add(p, args);
				}
				if (allowToDeny) {
					if (a0.equalsIgnoreCase(caption("CommandDeny"))) {
						return deny(p, args);
					}
					if (a0.equalsIgnoreCase(caption("CommandUndeny"))) {
						return undeny(p, args);
					}
				}
				if (a0.equalsIgnoreCase(caption("CommandRemove")) || a0.equalsIgnoreCase("-")) {
					return remove(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandSetowner")) || a0.equalsIgnoreCase("o")) {
					return setowner(p, args);
				}
//				if (a0.equalsIgnoreCase(C("CommandMove")) || a0.equalsIgnoreCase("m")) {
//					return move(p, args);
//				}
				if (a0.equalsIgnoreCase("reload")) {
					return reload(sender);
				}
				if (a0.equalsIgnoreCase(caption("CommandList"))) {
					return plotlist(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandExpired"))) {
					return expired(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandAddtime"))) {
					return addtime(p);
				}
				if (a0.equalsIgnoreCase(caption("CommandDone"))) {
					return done(p);
				}
				if (a0.equalsIgnoreCase(caption("CommandDoneList"))) {
					return donelist(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandProtect"))) {
					return protect(p);
				}
				if (a0.equalsIgnoreCase(caption("CommandSell"))) {
					return sell(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandDispose"))) {
					return dispose(p);
				}
				if (a0.equalsIgnoreCase(caption("CommandAuction"))) {
					return auction(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandBuy"))) {
					return buy(p);
				}
				if (a0.equalsIgnoreCase(caption("CommandBid"))) {
					return bid(p, args);
				}
				if (a0.startsWith(caption("CommandHome")) || a0.startsWith("h")) {
					return home(p, args);
				}
				if (a0.equalsIgnoreCase(caption("CommandResetExpired"))) {
					return resetexpired(p, args);
				}
			}
		}
		return false;
	}

	private boolean resetexpired(CommandSender sender, String[] args) {
		if (sender.hasPermission("PlotMe.admin.resetexpired")) {
			if (args.length <= 1) {
				Send(sender, caption("WordUsage") + ": /plotme " + caption("CommandResetExpired") + " <" + caption("WordWorld") + "> Example: /plotme " + caption("CommandResetExpired") + " plotworld ");
			} else if (worldcurrentlyprocessingexpired != null) {
				Send(sender, cscurrentlyprocessingexpired.getName() + " " + caption("MsgAlreadyProcessingPlots"));
			} else {
				World w = sender.getServer().getWorld(args[1]);

				if (w == null) {
					Send(sender, caption("WordWorld") + " '" + args[1] + "' " + caption("MsgDoesNotExistOrNotLoaded"));
					return true;
				}
				if (!PlotManager.isPlotWorld(w)) {
					Send(sender, caption("MsgNotPlotWorld"));
					return true;
				}
				worldcurrentlyprocessingexpired = w;
				cscurrentlyprocessingexpired = sender;
				counterexpired = 50;
				nbperdeletionprocessingexpired = 5;

				plugin.scheduleTask(new PlotRunnableDeleteExpire());
			}
		} else {
			Send(sender, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean bid(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			if (p.hasPermission("PlotMe.use.bid")) {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					Plot plot = PlotManager.getPlotById(p, id);

					if (plot.auctionned) {
						String bidder = p.getName();

						if (plot.owner.equalsIgnoreCase(bidder)) {
							Send(p, caption("MsgCannotBidOwnPlot"));
						} else if (args.length == 2) {
							double bid = 0;
							double currentbid = plot.currentbid;
							String currentbidder = plot.currentbidder;

							try {
								bid = Double.parseDouble(args[1]);
							} catch (NumberFormatException ignored) {
							}

							if (bid < currentbid || (bid == currentbid && !currentbidder.isEmpty())) {
								Send(p, caption("MsgInvalidBidMustBeAbove") + " " + f(plot.currentbid, false));
							} else {
								double balance = economy.getBalance(bidder);

								if (bid >= balance && !currentbidder.equals(bidder) || currentbidder.equals(bidder) && bid > (balance + currentbid)) {
									Send(p, caption("MsgNotEnoughBid"));
								} else {
									EconomyResponse er = economy.withdrawPlayer(bidder, bid);

									if (er.transactionSuccess()) {
										if (!currentbidder.isEmpty()) {
											EconomyResponse er2 = economy.depositPlayer(currentbidder, currentbid);

											if (!er2.transactionSuccess()) {
												Send(p, er2.errorMessage);
												warn(er2.errorMessage);
											} else {
												for (Player player : Bukkit.getServer().getOnlinePlayers()) {
													if (player.getName().equalsIgnoreCase(currentbidder)) {
														Send(player, caption("MsgOutbidOnPlot") + " " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + ". " + f(bid));
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

										Send(p, caption("MsgBidAccepted") + " " + f(-bid));

										logger.info(prefixe + bidder + " bid " + bid + " on plot " + id);
									} else {
										Send(p, er.errorMessage);
										warn(er.errorMessage);
									}
								}
							}
						} else {
							Send(p, caption("WordUsage") + ": /plotme " + caption("CommandBid") + " <" + caption("WordAmount") + "> " +
									"Example: /plotme " + caption("CommandBid") + " 100");
						}
					} else {
						Send(p, caption("MsgPlotNotAuctionned"));
					}
				} else {
					Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
				}
			} else {
				Send(p, caption("MsgPermissionDenied"));
			}
		} else {
			Send(p, caption("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean buy(Player p) {
		if (PlotManager.isEconomyEnabled(p)) {
			if (p.hasPermission("PlotMe.use.buy") || p.hasPermission("PlotMe.admin.buy")) {
				Location l = p.getLocation();
				String id = PlotManager.getPlotId(l);

				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					Plot plot = PlotManager.getPlotById(p, id);

					if (!plot.forsale) {
						Send(p, caption("MsgPlotNotForSale"));
					} else {
						String buyer = p.getName();

						if (plot.owner.equalsIgnoreCase(buyer)) {
							Send(p, caption("MsgCannotBuyOwnPlot"));
						} else {
							int plotlimit = getPlotLimit(p);

							if (plotlimit != -1 && PlotManager.getNbOwnedPlot(p) >= plotlimit) {
								Send(p, caption("MsgAlreadyReachedMaxPlots") + " (" +
										PlotManager.getNbOwnedPlot(p) + "/" + getPlotLimit(p) + "). " +
										"Use /plotme " + caption("CommandHome") + " " + caption("MsgToGetToIt"));
							} else {
								World w = p.getWorld();

								double cost = plot.customprice;

								if (economy.getBalance(buyer) < cost) {
									Send(p, caption("MsgNotEnoughBuy"));
								} else {
									EconomyResponse er = economy.withdrawPlayer(buyer, cost);

									if (er.transactionSuccess()) {
										String oldowner = plot.owner;

										if (!oldowner.equalsIgnoreCase("$Bank$")) {
											EconomyResponse er2 = economy.depositPlayer(oldowner, cost);

											if (!er2.transactionSuccess()) {
												Send(p, er2.errorMessage);
												warn(er2.errorMessage);
											} else {
												for (Player player : Bukkit.getServer().getOnlinePlayers()) {
													if (player.getName().equalsIgnoreCase(oldowner)) {
														Send(player, "Spot " + id + " " + caption("MsgSoldTo") + " " + buyer + ". " + f(cost));
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

										Send(p, caption("MsgPlotBought") + " " + f(-cost));

										logger.info(prefixe + buyer + " " + caption("MsgBoughtPlot") + " " + id + " for " + cost);
									} else {
										Send(p, er.errorMessage);
										warn(er.errorMessage);
									}
								}
							}
						}
					}
				} else {
					Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
				}
			} else {
				Send(p, caption("MsgPermissionDenied"));
			}
		} else {
			Send(p, caption("MsgEconomyDisabledWorld"));
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
						Send(p, caption("MsgNoPlotFound"));
					} else if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						String name = p.getName();

						if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.auction")) {
							World w = p.getWorld();

							if (plot.auctionned) {
								if (plot.currentbidder != null && !plot.currentbidder.equalsIgnoreCase("") && !p.hasPermission("PlotMe.admin.auction")) {
									Send(p, caption("MsgPlotHasBidsAskAdmin"));
								} else {
									if (plot.currentbidder != null && !plot.currentbidder.equalsIgnoreCase("")) {
										EconomyResponse er = economy.depositPlayer(plot.currentbidder, plot.currentbid);

										if (!er.transactionSuccess()) {
											Send(p, er.errorMessage);
											warn(er.errorMessage);
										} else {
											for (Player player : Bukkit.getServer().getOnlinePlayers()) {
												if (plot.currentbidder != null && player.getName().equalsIgnoreCase(plot.currentbidder)) {
													Send(player, caption("MsgAuctionCancelledOnPlot") + " " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + ". " + f(plot.currentbid));
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

									Send(p, caption("MsgAuctionCancelled"));

									logger.info(prefixe + name + " " + caption("MsgStoppedTheAuctionOnPlot") + " " + id);
								}
							} else {
								double bid = 1;

								if (args.length == 2) {
									try {
										bid = Double.parseDouble(args[1]);
									} catch (NumberFormatException ignored) {
									}
								}

								if (bid < 0) {
									Send(p, caption("MsgInvalidAmount"));
								} else {
									plot.currentbid = bid;
									plot.auctionned = true;
									PlotManager.adjustWall(p.getLocation());
									PlotManager.setSellSign(w, plot);

									plot.updateField("currentbid", bid);
									plot.updateField("auctionned", true);

									Send(p, caption("MsgAuctionStarted"));

									logger.info(prefixe + name + " " + caption("MsgStartedAuctionOnPlot") + " " + id + " at " + bid);
								}
							}
						} else {
							Send(p, caption("MsgDoNotOwnPlot"));
						}
					} else {
						Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
					}
				} else {
					Send(p, caption("MsgPermissionDenied"));
				}
			} else {
				Send(p, caption("MsgSellingPlotsIsDisabledWorld"));
			}
		} else {
			Send(p, caption("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean dispose(Player p) {
		if (p.hasPermission("PlotMe.admin.dispose") || p.hasPermission("PlotMe.use.dispose")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot.protect) {
							Send(p, caption("MsgPlotProtectedNotDisposed"));
						} else {
							String name = p.getName();

							if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.dispose")) {
								PlotMapInfo pmi = PlotManager.getMap(p);

								double cost = pmi.DisposePrice;

								if (PlotManager.isEconomyEnabled(p)) {
									if (cost != 0 && economy.getBalance(name) < cost) {
										Send(p, caption("MsgNotEnoughDispose"));
										return true;
									}

									EconomyResponse er = economy.withdrawPlayer(name, cost);

									if (!er.transactionSuccess()) {
										Send(p, er.errorMessage);
										warn(er.errorMessage);
										return true;
									}

									if (plot.auctionned) {
										String currentbidder = plot.currentbidder;

										if (!currentbidder.isEmpty()) {
											EconomyResponse er2 = economy.depositPlayer(currentbidder, plot.currentbid);

											if (!er2.transactionSuccess()) {
												Send(p, er2.errorMessage);
												warn(er2.errorMessage);
											} else {
												for (Player player : Bukkit.getServer().getOnlinePlayers()) {
													if (player.getName().equalsIgnoreCase(currentbidder)) {
														Send(player, "Spot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgWasDisposed") + " " + f(cost));
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

								Send(p, caption("MsgPlotDisposedAnyoneClaim"));

								logger.info(prefixe + name + " " + caption("MsgDisposedPlot") + " " + id);
							} else {
								Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgNotYoursCannotDispose"));
							}
						}
					} else {
						Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean sell(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			PlotMapInfo pmi = PlotManager.getMap(p);

			if (pmi.CanSellToBank || pmi.CanPutOnSale) {
				if (p.hasPermission("PlotMe.use.sell")) {
					Location l = p.getLocation();
					String id = PlotManager.getPlotId(l);

					if (id.isEmpty()) {
						Send(p, caption("MsgNoPlotFound"));
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

									Send(p, caption("MsgPlotNoLongerSale"));

									logger.info(prefixe + name + " " + caption("MsgRemovedPlot") + " " + id + " " + caption("MsgFromBeingSold"));
								} else {
									double price = pmi.SellToPlayerPrice;
									boolean bank = false;

									if (args.length == 2) {
										if (args[1].equalsIgnoreCase("bank")) {
											bank = true;
										} else if (pmi.CanCustomizeSellPrice) {
											try {
												price = Double.parseDouble(args[1]);
											} catch (Exception e) {
												if (pmi.CanSellToBank) {
													Send(p, caption("WordUsage") + ":  /plotme " + caption("CommandSellBank") + "|<" + caption("WordAmount") + ">");
													p.sendMessage("  Example: /plotme " + caption("CommandSellBank") + " or /plotme " + caption("CommandSell") + " 200");
												} else {
													Send(p, caption("WordUsage") + ": /plotme " + caption("CommandSell") + " <" + caption("WordAmount") + "> Example: /plotme " + caption("CommandSell") + " 200");
												}
											}
										} else {
											Send(p, caption("MsgCannotCustomPriceDefault") + " " + price);
											return true;
										}
									}

									if (bank) {
										if (!pmi.CanSellToBank) {
											Send(p, caption("MsgCannotSellToBank"));
										} else {

											String currentbidder = plot.currentbidder;

											if (!currentbidder.isEmpty()) {
												double bid = plot.currentbid;

												EconomyResponse er = economy.depositPlayer(currentbidder, bid);

												if (!er.transactionSuccess()) {
													Send(p, er.errorMessage);
													warn(er.errorMessage);
												} else {
													for (Player player : Bukkit.getServer().getOnlinePlayers()) {
														if (player.getName().equalsIgnoreCase(currentbidder)) {
															Send(player, "Spot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgSoldToBank") + " " + f(bid));
															break;
														}
													}
												}
											}

											double sellprice = pmi.SellToBankPrice;

											EconomyResponse er = economy.depositPlayer(name, sellprice);

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

												Send(p, caption("MsgPlotSold") + " " + f(sellprice));

												logger.info(prefixe + name + " " + caption("MsgSoldToBankPlot") + " " + id + " for " + sellprice);
											} else {
												Send(p, " " + er.errorMessage);
												warn(er.errorMessage);
											}
										}
									} else if (price < 0) {
										Send(p, caption("MsgInvalidAmount"));
									} else {
										plot.customprice = price;
										plot.forsale = true;

										plot.updateField("customprice", price);
										plot.updateField("forsale", true);

										PlotManager.adjustWall(l);
										PlotManager.setSellSign(w, plot);

										Send(p, caption("MsgPlotForSale"));

										logger.info(prefixe + name + " " + caption("MsgPutOnSalePlot") + " " + id + " for " + price);
									}
								}
							} else {
								Send(p, caption("MsgDoNotOwnPlot"));
							}
						} else {
							Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
						}
					}
				} else if (p.hasPermission("PlotMe.admin.sell")) {
					Location l = p.getLocation();
					String id = PlotManager.getPlotId(l);

					if (id.isEmpty()) {
						Send(p, caption("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							World w = p.getWorld();
							String name = p.getName();

							if (plot.forsale) {
								plot.customprice = 0;
								plot.forsale = false;

								plot.updateField("customprice", 0);
								plot.updateField("forsale", false);

								PlotManager.adjustWall(l);
								PlotManager.setSellSign(w, plot);

								Send(p, caption("MsgPlotNoLongerSale"));

								logger.info(prefixe + name + " " + caption("MsgRemovedPlot") + " " + id + " " + caption("MsgFromBeingSold"));
							} else {
								double price = pmi.SellToPlayerPrice;
								boolean bank = false;

								if (args.length == 2) {
									if (args[1].equalsIgnoreCase("bank")) {
										bank = true;
									} else if (pmi.CanCustomizeSellPrice) {
										try {
											price = Double.parseDouble(args[1]);
										} catch (Exception e) {
											if (pmi.CanSellToBank) {
												Send(p, caption("WordUsage") + ":  /plotme " + caption("CommandSellBank") + "|<" + caption("WordAmount") + ">");
												p.sendMessage("  Example: /plotme " + caption("CommandSellBank") + " or /plotme " + caption("CommandSell") + " 200");
											} else {
												Send(p, caption("WordUsage") + ": /plotme " + caption("CommandSell") + " <" + caption("WordAmount") + "> Example: /plotme " + caption("CommandSell") + " 200");
											}
										}
									} else {
										Send(p, caption("MsgCannotCustomPriceDefault") + " " + price);
										return true;
									}
								}

								if (bank) {
									if (!pmi.CanSellToBank) {
										Send(p, caption("MsgCannotSellToBank"));
									} else {

										String currentbidder = plot.currentbidder;

										if (!currentbidder.isEmpty()) {
											double bid = plot.currentbid;

											EconomyResponse er = economy.depositPlayer(currentbidder, bid);

											if (!er.transactionSuccess()) {
												Send(p, er.errorMessage);
												warn(er.errorMessage);
											} else {
												for (Player player : Bukkit.getServer().getOnlinePlayers()) {
													if (player.getName().equalsIgnoreCase(currentbidder)) {
														Send(player, "Spot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgSoldToBank") + " " + f(bid));
														break;
													}
												}
											}
										}

										double sellprice = pmi.SellToBankPrice;

										EconomyResponse er = economy.depositPlayer(name, sellprice);

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

											Send(p, caption("MsgPlotSold") + " " + f(sellprice));

											logger.info(prefixe + name + " " + caption("MsgSoldToBankPlot") + " " + id + " for " + sellprice);
										} else {
											Send(p, " " + er.errorMessage);
											warn(er.errorMessage);
										}
									}
								} else if (price < 0) {
									Send(p, caption("MsgInvalidAmount"));
								} else {
									plot.customprice = price;
									plot.forsale = true;

									plot.updateField("customprice", price);
									plot.updateField("forsale", true);

									PlotManager.adjustWall(l);
									PlotManager.setSellSign(w, plot);

									Send(p, caption("MsgPlotForSale"));

									logger.info(prefixe + name + " " + caption("MsgPutOnSalePlot") + " " + id + " for " + price);
								}
							}
						} else {
							Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
						}
					}
				} else {
					Send(p, caption("MsgPermissionDenied"));
				}
			} else {
				Send(p, caption("MsgSellingPlotsIsDisabledWorld"));
			}
		} else {
			Send(p, caption("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean protect(Player p) {
		if (p.hasPermission("PlotMe.admin.protect") || p.hasPermission("PlotMe.use.protect")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
				return true;
			}
			String id = PlotManager.getPlotId(p.getLocation());

			if (id.isEmpty()) {
				Send(p, caption("MsgNoPlotFound"));
			} else if (!PlotManager.isPlotAvailable(id, p)) {
				Plot plot = PlotManager.getPlotById(p, id);

				String name = p.getName();

				if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.protect")) {
					if (plot.protect) {
						plot.protect = false;
						PlotManager.adjustWall(p.getLocation());

						plot.updateField("protected", false);

						Send(p, caption("MsgPlotNoLongerProtected"));

						logger.info(prefixe + name + " " + caption("MsgUnprotectedPlot") + " " + id);
					} else {
						PlotMapInfo pmi = PlotManager.getMap(p);

						double cost = 0;

						if (PlotManager.isEconomyEnabled(p)) {
							cost = pmi.ProtectPrice;

							if (economy.getBalance(name) < cost) {
								Send(p, caption("MsgNotEnoughProtectPlot"));
								return true;
							}
							EconomyResponse er = economy.withdrawPlayer(name, cost);

							if (!er.transactionSuccess()) {
								Send(p, er.errorMessage);
								warn(er.errorMessage);
								return true;
							}

						}

						plot.protect = true;
						PlotManager.adjustWall(p.getLocation());

						plot.updateField("protected", true);

						Send(p, caption("MsgPlotNowProtected") + " " + f(-cost));

						logger.info(prefixe + name + " " + caption("MsgProtectedPlot") + " " + id);

					}
				} else {
					Send(p, caption("MsgDoNotOwnPlot"));
				}
			} else {
				Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean donelist(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.done")) {
			PlotMapInfo pmi = PlotManager.getMap(p);

			if (pmi == null) {
				Send(p, caption("MsgNotPlotWorld"));
				return true;
			}
			HashMap<String, Plot> plots = pmi.plots;
			List<Plot> finishedplots = new ArrayList<>();
			int nbfinished = 0;
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

			int maxpage = (int) Math.ceil((double) nbfinished / (double) pagesize);

			if (finishedplots.isEmpty()) {
				Send(p, caption("MsgNoPlotsFinished"));
			} else {
				Send(p, caption("MsgFinishedPlotsPage") + " " + page + "/" + maxpage);

				for (int i = (page - 1) * pagesize; i < finishedplots.size() && i < (page * pagesize); i++) {
					Plot plot = finishedplots.get(i);

					String starttext = "  " + plot.id + " -> " + plot.owner;

					int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);

					String line = starttext + whitespace(550 - textLength) + "@" + plot.finisheddate;

					p.sendMessage(line);
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean done(Player p) {
		if (p.hasPermission("PlotMe.use.done") || p.hasPermission("PlotMe.admin.done")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
				return true;
			}
			String id = PlotManager.getPlotId(p.getLocation());

			if (id.isEmpty()) {
				Send(p, caption("MsgNoPlotFound"));
			} else if (!PlotManager.isPlotAvailable(id, p)) {
				Plot plot = PlotManager.getPlotById(p, id);
				String name = p.getName();

				if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.done")) {
					if (plot.finished) {
						plot.setUnfinished();
						Send(p, caption("MsgUnmarkFinished"));

						logger.info(prefixe + name + " " + caption("WordMarked") + " " + id + " " + caption("WordFinished"));
					} else {
						plot.setFinished();
						Send(p, caption("MsgMarkFinished"));

						logger.info(prefixe + name + " " + caption("WordMarked") + " " + id + " " + caption("WordUnfinished"));
					}
				}
			} else {
				Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean addtime(Player p) {
		if (p.hasPermission("PlotMe.admin.addtime")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
				return true;
			}
			String id = PlotManager.getPlotId(p.getLocation());

			if (id.isEmpty()) {
				Send(p, caption("MsgNoPlotFound"));
			} else if (!PlotManager.isPlotAvailable(id, p)) {
				Plot plot = PlotManager.getPlotById(p, id);

				if (plot != null) {
					String name = p.getName();

					plot.resetExpire(PlotManager.getMap(p).DaysToExpiration);
					Send(p, caption("MsgPlotExpirationReset"));

					logger.info(prefixe + name + " reset expiration on plot " + id);
				}
			} else {
				Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean expired(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.expired")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
				return true;
			}
			int pagesize = 8;
			int page = 1;
			int nbexpiredplots = 0;
			World w = p.getWorld();
			List<Plot> expiredplots = new ArrayList<>();
			HashMap<String, Plot> plots = PlotManager.getPlots(w);
			String date = getDate();

			if (args.length == 2) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException ignored) {
				}
			}

			for (String id : plots.keySet()) {
				Plot plot = plots.get(id);

				if (!plot.protect && plot.expireddate != null && getDate(plot.expireddate).compareTo(date) < 0) {
					nbexpiredplots++;
					expiredplots.add(plot);
				}
			}

			Collections.sort(expiredplots);


			if (expiredplots.isEmpty()) {
				Send(p, caption("MsgNoPlotExpired"));
			} else {
				int maxpage = (int) Math.ceil((double) nbexpiredplots / (double) pagesize);
				Send(p, caption("MsgExpiredPlotsPage") + " " + page + "/" + maxpage);

				for (int i = (page - 1) * pagesize; i < expiredplots.size() && i < (page * pagesize); i++) {
					Plot plot = expiredplots.get(i);

					String starttext = "  " + plot.id + " -> " + plot.owner;

					int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);

					String line = starttext + whitespace(550 - textLength) + "@" + plot.expireddate;

					p.sendMessage(line);
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}


	private boolean plotlist(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.list")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
				return true;
			}
			String name;
			String pname = p.getName();

			if (p.hasPermission("PlotMe.admin.list") && args.length == 2) {
				name = args[1];
				Send(p, caption("MsgListOfPlotsWhere") + " " + name + " " + caption("MsgCanBuild"));
			} else {
				name = p.getName();
				Send(p, caption("MsgListOfPlotsWhereYou"));
			}

			for (Plot plot : PlotManager.getPlots(p).values()) {
				StringBuilder addition = new StringBuilder();

				if (plot.expireddate != null) {

					addition.append(" @" + plot.expireddate);
				}

				if (plot.auctionned) {
					if (plot.currentbidder != null && !plot.currentbidder.isEmpty()) {
						addition.append(" " + caption("WordAuction") + ": " + round(plot.currentbid) + (" " + plot.currentbidder));
					} else {
						addition.append(" " + caption("WordAuction") + ": " + round(plot.currentbid));
					}
				}

				if (plot.forsale) {
					addition.append(" " + caption("WordSell") + ": " + round(plot.customprice));
				}

				if (plot.owner.equalsIgnoreCase(name)) {
					if (plot.allowedcount() == 0) {
						if (name.equalsIgnoreCase(pname)) {
							p.sendMessage("  " + plot.id + " -> " + caption("WordYours") + addition);
						} else {
							p.sendMessage("  " + plot.id + " -> " + plot.owner + addition);
						}
					} else {
						StringBuilder helpers = new StringBuilder();
						for (int i = 0; i < plot.allowedcount(); i++) {
							helpers.append(plot.allowed().toArray()[i]).append(", ");
						}
						if (helpers.length() > 2) {
							helpers.delete(helpers.length() - 2, helpers.length());
						}

						if (name.equalsIgnoreCase(pname)) {
							p.sendMessage("  " + plot.id + " -> " + caption("WordYours") + addition + ", " + caption("WordHelpers") + ": " + helpers);
						} else {
							p.sendMessage("  " + plot.id + " -> " + plot.owner + addition + ", " + caption("WordHelpers") + ": " + helpers);
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
						p.sendMessage("  " + plot.id + " -> " + caption("WordYours") + addition + ", " + caption("WordHelpers") + ": " + helpers);
					} else {
						p.sendMessage("  " + plot.id + " -> " + plot.owner + caption("WordPossessive") + addition + ", " + caption("WordHelpers") + ": " + helpers);
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean showhelp(Player player, int page) {
		int max = 4;
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
		if (allowToDeny) {
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
//		if (player.hasPermission("PlotMe.admin.move")) {
//			allowed_commands.add("move");
//		}
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

		int maxpage = (int) Math.ceil((double) allowed_commands.size() / max);

		if (page > maxpage) {
			page = 1;
		}

		player.sendMessage(" ---==" + caption("HelpTitle") + " " + page + "/" + maxpage + "==--- ");

		for (int ctr = (page - 1) * max; ctr < (page * max) && ctr < allowed_commands.size(); ctr++) {
			String allowedcmd = allowed_commands.get(ctr);

			if (allowedcmd.equalsIgnoreCase("limit")) {
				boolean plotWorld = PlotManager.isPlotWorld(player);
				World w = null;

				if (plotWorld) {
					w = player.getWorld();
				} else if (allowWorldTeleport) {
					w = PlotManager.getFirstWorld();
				}

				int maxplots = getPlotLimit(player);
				int ownedplots = PlotManager.getNbOwnedPlot(player, w);

				if (maxplots == -1) {
					player.sendMessage(caption("HelpYourPlotLimitWorld") + " : " + ownedplots +
							" " + caption("HelpUsedOf") + " " + caption("WordInfinite"));
				} else {
					player.sendMessage(caption("HelpYourPlotLimitWorld") + " : " + ownedplots +
							" " + caption("HelpUsedOf") + " " + maxplots);
				}
			} else if (allowedcmd.equalsIgnoreCase("claim")) {
				player.sendMessage(" /plotme " + caption("CommandClaim"));
				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					player.sendMessage(" " + caption("HelpClaim") + " " + caption("WordPrice") + " : " + round(pmi.ClaimPrice));
				} else {
					player.sendMessage(" " + caption("HelpClaim"));
				}
			} else if (allowedcmd.equalsIgnoreCase("claim.other")) {
				player.sendMessage(" /plotme " + caption("CommandClaim") + " <" + caption("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					player.sendMessage(" " + caption("HelpClaimOther") + " " + caption("WordPrice") + " : " + round(pmi.ClaimPrice));
				} else {
					player.sendMessage(" " + caption("HelpClaimOther"));
				}
			} else if (allowedcmd.equalsIgnoreCase("auto")) {
				if (allowWorldTeleport) {
					player.sendMessage(" /plotme " + caption("CommandAuto") + " [" + caption("WordWorld") + "]");
				} else {
					player.sendMessage(" /plotme " + caption("CommandAuto"));
				}

				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					player.sendMessage(" " + caption("HelpAuto") + " " + caption("WordPrice") + " : " + round(pmi.ClaimPrice));
				} else {
					player.sendMessage(" " + caption("HelpAuto"));
				}
			} else if (allowedcmd.equalsIgnoreCase("home")) {
				if (allowWorldTeleport) {
					player.sendMessage(" /plotme " + caption("CommandHome") + "[:#] [" + caption("WordWorld") + "]");
				} else {
					player.sendMessage(" /plotme " + caption("CommandHome") + "[:#]");
				}

				if (ecoon && pmi != null && pmi.PlotHomePrice != 0) {
					player.sendMessage(" " + caption("HelpHome") + " " + caption("WordPrice") + " : " + round(pmi.PlotHomePrice));
				} else {
					player.sendMessage(" " + caption("HelpHome"));
				}
			} else if (allowedcmd.equalsIgnoreCase("home.other")) {
				if (allowWorldTeleport) {
					player.sendMessage(" /plotme " + caption("CommandHome") + "[:#] <" + caption("WordPlayer") + "> [" + caption("WordWorld") + "]");
				} else {
					player.sendMessage(" /plotme " + caption("CommandHome") + "[:#] <" + caption("WordPlayer") + ">");
				}

				if (ecoon && pmi != null && pmi.PlotHomePrice != 0) {
					player.sendMessage(" " + caption("HelpHomeOther") + " " + caption("WordPrice") + " : " + round(pmi.PlotHomePrice));
				} else {
					player.sendMessage(" " + caption("HelpHomeOther"));
				}
			} else if (allowedcmd.equalsIgnoreCase("info")) {
				player.sendMessage(" /plotme " + caption("CommandInfo"));
				player.sendMessage(" " + caption("HelpInfo"));
			} else if (allowedcmd.equalsIgnoreCase("comment")) {
				player.sendMessage(" /plotme " + caption("CommandComment") + " <" + caption("WordComment") + ">");
				if (ecoon && pmi != null && pmi.AddCommentPrice != 0) {
					player.sendMessage(" " + caption("HelpComment") + " " + caption("WordPrice") + " : " + round(pmi.AddCommentPrice));
				} else {
					player.sendMessage(" " + caption("HelpComment"));
				}
			} else if (allowedcmd.equalsIgnoreCase("comments")) {
				player.sendMessage(" /plotme " + caption("CommandComments"));
				player.sendMessage(" " + caption("HelpComments"));
			} else if (allowedcmd.equalsIgnoreCase("list")) {
				player.sendMessage(" /plotme " + caption("CommandList"));
				player.sendMessage(" " + caption("HelpList"));
			} else if (allowedcmd.equalsIgnoreCase("listother")) {
				player.sendMessage(" /plotme " + caption("CommandList") + " <" + caption("WordPlayer") + ">");
				player.sendMessage(" " + caption("HelpListOther"));
			} else if (allowedcmd.equalsIgnoreCase("biomeinfo")) {
				player.sendMessage(" /plotme " + caption("CommandBiome"));
				player.sendMessage(" " + caption("HelpBiomeInfo"));
			} else if (allowedcmd.equalsIgnoreCase("biome")) {
				player.sendMessage(" /plotme " + caption("CommandBiome") + " <" + caption("WordBiome") + ">");
				if (ecoon && pmi != null && pmi.BiomeChangePrice != 0) {
					player.sendMessage(" " + caption("HelpBiome") + " " + caption("WordPrice") + " : " + round(pmi.BiomeChangePrice));
				} else {
					player.sendMessage(" " + caption("HelpBiome"));
				}
			} else if (allowedcmd.equalsIgnoreCase("biomelist")) {
				player.sendMessage(" /plotme " + caption("CommandBiomelist"));
				player.sendMessage(" " + caption("HelpBiomeList"));
			} else if (allowedcmd.equalsIgnoreCase("done")) {
				player.sendMessage(" /plotme " + caption("CommandDone"));
				player.sendMessage(" " + caption("HelpDone"));
			} else if (allowedcmd.equalsIgnoreCase("tp")) {
				if (allowWorldTeleport) {
					player.sendMessage(" /plotme " + caption("CommandTp") + " <id> [" + caption("WordWorld") + "]");
				} else {
					player.sendMessage(" /plotme " + caption("CommandTp") + " <id>");
				}

				player.sendMessage(" " + caption("HelpTp"));
			} else if (allowedcmd.equalsIgnoreCase("id")) {
				player.sendMessage(" /plotme " + caption("CommandId"));
				player.sendMessage(" " + caption("HelpId"));
			} else if (allowedcmd.equalsIgnoreCase("clear")) {
				player.sendMessage(" /plotme " + caption("CommandClear"));
				if (ecoon && pmi != null && pmi.ClearPrice != 0) {
					player.sendMessage(" " + caption("HelpId") + " " + caption("WordPrice") + " : " + round(pmi.ClearPrice));
				} else {
					player.sendMessage(" " + caption("HelpClear"));
				}
			} else if (allowedcmd.equalsIgnoreCase("reset")) {
				player.sendMessage(" /plotme " + caption("CommandReset"));
				player.sendMessage(" " + caption("HelpReset"));
			} else if (allowedcmd.equalsIgnoreCase("add")) {
				player.sendMessage(" /plotme " + caption("CommandAdd") + " <" + caption("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.AddPlayerPrice != 0) {
					player.sendMessage(" " + caption("HelpAdd") + " " + caption("WordPrice") + " : " + round(pmi.AddPlayerPrice));
				} else {
					player.sendMessage(" " + caption("HelpAdd"));
				}
			} else if (allowedcmd.equalsIgnoreCase("deny")) {
				player.sendMessage(" /plotme " + caption("CommandDeny") + " <" + caption("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.DenyPlayerPrice != 0) {
					player.sendMessage(" " + caption("HelpDeny") + " " + caption("WordPrice") + " : " + round(pmi.DenyPlayerPrice));
				} else {
					player.sendMessage(" " + caption("HelpDeny"));
				}
			} else if (allowedcmd.equalsIgnoreCase("remove")) {
				player.sendMessage(" /plotme " + caption("CommandRemove") + " <" + caption("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.RemovePlayerPrice != 0) {
					player.sendMessage(" " + caption("HelpRemove") + " " + caption("WordPrice") + " : " + round(pmi.RemovePlayerPrice));
				} else {
					player.sendMessage(" " + caption("HelpRemove"));
				}
			} else if (allowedcmd.equalsIgnoreCase("undeny")) {
				player.sendMessage(" /plotme " + caption("CommandUndeny") + " <" + caption("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.UndenyPlayerPrice != 0) {
					player.sendMessage(" " + caption("HelpUndeny") + " " + caption("WordPrice") + " : " + round(pmi.UndenyPlayerPrice));
				} else {
					player.sendMessage(" " + caption("HelpUndeny"));
				}
			} else if (allowedcmd.equalsIgnoreCase("setowner")) {
				player.sendMessage(" /plotme " + caption("CommandSetowner") + " <" + caption("WordPlayer") + ">");
				player.sendMessage(" " + caption("HelpSetowner"));
			} else if (allowedcmd.equalsIgnoreCase("move")) {
				player.sendMessage(" /plotme " + caption("CommandMove") + " <" + caption("WordIdFrom") + "> <" + caption("WordIdTo") + ">");
				player.sendMessage(" " + caption("HelpMove"));
			} else if (allowedcmd.equalsIgnoreCase("expired")) {
				player.sendMessage(" /plotme " + caption("CommandExpired") + " [page]");
				player.sendMessage(" " + caption("HelpExpired"));
			} else if (allowedcmd.equalsIgnoreCase("donelist")) {
				player.sendMessage(" /plotme " + caption("CommandDoneList") + " [page]");
				player.sendMessage(" " + caption("HelpDoneList"));
			} else if (allowedcmd.equalsIgnoreCase("addtime")) {
				player.sendMessage(" /plotme " + caption("CommandAddtime"));
				int days;
				if (pmi == null) {
					days = 0;
				} else {
					days = pmi.DaysToExpiration;
				}
				if (days == 0) {
					player.sendMessage(" " + caption("HelpAddTime1") + " " + caption("WordNever"));
				} else {
					player.sendMessage(" " + caption("HelpAddTime1") + " " + days + " " + caption("HelpAddTime2"));
				}
			} else if (allowedcmd.equalsIgnoreCase("reload")) {
				player.sendMessage(" /plotme reload");
				player.sendMessage(" " + caption("HelpReload"));
			} else if (allowedcmd.equalsIgnoreCase("dispose")) {
				player.sendMessage(" /plotme " + caption("CommandDispose"));
				if (ecoon && pmi != null && pmi.DisposePrice != 0) {
					player.sendMessage(" " + caption("HelpDispose") + " " + caption("WordPrice") + " : " + round(pmi.DisposePrice));
				} else {
					player.sendMessage(" " + caption("HelpDispose"));
				}
			} else if (allowedcmd.equalsIgnoreCase("buy")) {
				player.sendMessage(" /plotme " + caption("CommandBuy"));
				player.sendMessage(" " + caption("HelpBuy"));
			} else if (allowedcmd.equalsIgnoreCase("sell")) {
				player.sendMessage(" /plotme " + caption("CommandSell") + " [" + caption("WordAmount") + "]");
				player.sendMessage(" " + caption("HelpSell") + " " + caption("WordDefault") + " : " + round(pmi.SellToPlayerPrice));
			} else if (allowedcmd.equalsIgnoreCase("sellbank")) {
				player.sendMessage(" /plotme " + caption("CommandSellBank"));
				player.sendMessage(" " + caption("HelpSellBank") + " " + round(pmi.SellToBankPrice));
			} else if (allowedcmd.equalsIgnoreCase("auction")) {
				player.sendMessage(" /plotme " + caption("CommandAuction") + " [" + caption("WordAmount") + "]");
				player.sendMessage(" " + caption("HelpAuction") + " " + caption("WordDefault") + " : 1");
			} else if (allowedcmd.equalsIgnoreCase("resetexpired")) {
				player.sendMessage(" /plotme " + caption("CommandResetExpired") + " <" + caption("WordWorld") + ">");
				player.sendMessage(" " + caption("HelpResetExpired"));
			} else if (allowedcmd.equalsIgnoreCase("bid")) {
				player.sendMessage(" /plotme " + caption("CommandBid") + " <" + caption("WordAmount") + ">");
				player.sendMessage(" " + caption("HelpBid"));
			}
		}

		return true;
	}

	private boolean tp(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.tp")) {
			if (!PlotManager.isPlotWorld(p) && !allowWorldTeleport) {
				Send(p, caption("MsgNotPlotWorld"));
			} else if (args.length == 2 || (args.length == 3 && allowWorldTeleport)) {
				String id = args[1];

				if (!PlotManager.isValidId(id)) {
					if (allowWorldTeleport) {
						Send(p, caption("WordUsage") + ": /plotme " + caption("CommandTp") + " <id> [" + caption("WordWorld") + "] Example: /plotme " + caption("CommandTp") + " 5;-1 ");
					} else {
						Send(p, caption("WordUsage") + ": /plotme " + caption("CommandTp") + " <id> Example: /plotme " + caption("CommandTp") + " 5;-1 ");
					}
					return true;
				}
				World w;

				if (args.length == 3) {
					String world = args[2];

					w = Bukkit.getWorld(world);
				} else if (!PlotManager.isPlotWorld(p)) {
					w = PlotManager.getFirstWorld();
				} else {
					w = p.getWorld();
				}

				if (w == null || !PlotManager.isPlotWorld(w)) {
					Send(p, caption("MsgNoPlotworldFound"));
				} else {
					Location bottom = PlotManager.getPlotBottomLoc(w, id);
					Location top = PlotManager.getPlotTopLoc(w, id);

					p.teleport(new Location(w, bottom.getX() + (top.getBlockX() - bottom.getBlockX()) / 2, PlotManager.getMap(w).RoadHeight + 2, bottom.getZ() - 2));
				}
			} else if (allowWorldTeleport) {
				Send(p, caption("WordUsage") + ": /plotme " + caption("CommandTp") + " <id> [" + caption("WordWorld") + "] Example: /plotme " + caption("CommandTp") + " 5;-1 ");
			} else {
				Send(p, caption("WordUsage") + ": /plotme " + caption("CommandTp") + " <id> Example: /plotme " + caption("CommandTp") + " 5;-1 ");
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean auto(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.auto")) {
			if (!PlotManager.isPlotWorld(p)) {
				if (!allowWorldTeleport) {
					Send(p, caption("MsgNotPlotWorld"));
				} else {
					World w;

					if (args.length == 2) {
						w = Bukkit.getWorld(args[1]);
					} else {
						w = PlotManager.getFirstWorld();
					}

					if (w == null) {
						Send(p, args[1] + " does not exist or is not a plot world.");
						return true;
					}
					if (!PlotManager.isPlotWorld(w)) {
						Send(p, args[1] + " does not exist or is not a plot world.");
						return true;
					}

					if (PlotManager.getNbOwnedPlot(p, w) >= getPlotLimit(p) && !p.hasPermission("PlotMe.admin")) {
						Send(p, caption("MsgAlreadyReachedMaxPlots") + " (" +
								PlotManager.getNbOwnedPlot(p, w) + "/" + getPlotLimit(p) + "). Use /plotme " + caption("CommandHome") + " " + caption("MsgToGetToIt"));
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
											double balance = economy.getBalance(name);

											if (balance >= price) {
												EconomyResponse er = economy.withdrawPlayer(name, price);

												if (!er.transactionSuccess()) {
													Send(p, er.errorMessage);
													warn(er.errorMessage);
													return true;
												}
											} else {
												Send(p, caption("MsgNotEnoughAuto") + " " + caption("WordMissing") + " " + f(price - balance, false));
												return true;
											}
										}

										Plot plot = PlotManager.createPlot(w, id, name, uuid);

										//PlotManager.adjustLinkedPlots(id, w);

										p.teleport(new Location(w, PlotManager.bottomX(plot.id, w) + (PlotManager.topX(plot.id, w) - PlotManager.bottomX(plot.id, w)) / 2, pmi.RoadHeight + 2, PlotManager.bottomZ(plot.id, w) - 2));

										Send(p, caption("MsgThisPlotYours") + " Use /plotme " + caption("CommandHome") + " " + caption("MsgToGetToIt") + " " + f(-price));

										if (price != 0) {
											logger.info(prefixe + name + " " + caption("MsgClaimedPlot") + " " + id + (" for " + price));
										} else {
											logger.info(prefixe + name + " " + caption("MsgClaimedPlot") + " " + id + "");
										}

										return true;
									}
								}
							}
						}

						Send(p, caption("MsgNoPlotFound1") + " " + (limit ^ 2) + " " + caption("MsgNoPlotFound2"));
					}
				}
			} else {
				World w;

				w = p.getWorld();

				if (w == null) {
					Send(p, caption("MsgNoPlotworldFound"));
				} else if (PlotManager.getNbOwnedPlot(p, w) >= getPlotLimit(p) && !p.hasPermission("PlotMe.admin")) {
					Send(p, caption("MsgAlreadyReachedMaxPlots") + " (" +
							PlotManager.getNbOwnedPlot(p, w) + "/" + getPlotLimit(p) + "). Use /plotme " + caption("CommandHome") + " " + caption("MsgToGetToIt"));
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
										double balance = economy.getBalance(name);

										if (balance >= price) {
											EconomyResponse er = economy.withdrawPlayer(name, price);

											if (!er.transactionSuccess()) {
												Send(p, er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, caption("MsgNotEnoughAuto") + " " + caption("WordMissing") + " " + f(price - balance, false));
											return true;
										}
									}

									Plot plot = PlotManager.createPlot(w, id, name, uuid);

									//PlotManager.adjustLinkedPlots(id, w);

									p.teleport(new Location(w, PlotManager.bottomX(plot.id, w) + (PlotManager.topX(plot.id, w) - PlotManager.bottomX(plot.id, w)) / 2, pmi.RoadHeight + 2, PlotManager.bottomZ(plot.id, w) - 2));

									Send(p, caption("MsgThisPlotYours") + " Use /plotme " + caption("CommandHome") + " " + caption("MsgToGetToIt") + " " + f(-price));

									if (price != 0) {
										logger.info(prefixe + name + " " + caption("MsgClaimedPlot") + " " + id + (" for " + price));
									} else {
										logger.info(prefixe + name + " " + caption("MsgClaimedPlot") + " " + id);
									}

									return true;
								}
							}
						}
					}

					Send(p, caption("MsgNoPlotFound1") + " " + (limit ^ 2) + " " + caption("MsgNoPlotFound2"));
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean claim(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.claim") || p.hasPermission("PlotMe.admin.claim.other")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, caption("MsgCannotClaimRoad"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					Send(p, caption("MsgThisPlotOwned"));
				} else {
					String playername = p.getName();
					UUID uuid = p.getUniqueId();

					if (args.length == 2) {
						if (p.hasPermission("PlotMe.admin.claim.other")) {
							playername = args[1];
							uuid = null;
						}
					}

					int plotlimit = getPlotLimit(p);

					if (playername.equals(p.getName()) && plotlimit != -1 && PlotManager.getNbOwnedPlot(p) >= plotlimit) {
						Send(p, caption("MsgAlreadyReachedMaxPlots") + " (" + PlotManager.getNbOwnedPlot(p) + "/" + getPlotLimit(p) + "). Use /plotme " + caption("CommandHome") + " " + caption("MsgToGetToIt"));
					} else {
						World w = p.getWorld();
						PlotMapInfo pmi = PlotManager.getMap(w);

						double price = 0;

						if (PlotManager.isEconomyEnabled(w)) {
							price = pmi.ClaimPrice;
							double balance = economy.getBalance(playername);

							if (balance >= price) {
								EconomyResponse er = economy.withdrawPlayer(playername, price);

								if (!er.transactionSuccess()) {
									Send(p, er.errorMessage);
									warn(er.errorMessage);
									return true;
								}
							} else {
								Send(p, caption("MsgNotEnoughBuy") + " " + caption("WordMissing") + " " + (price - balance) + " " + economy.currencyNamePlural());
								return true;
							}
						}

						Plot plot = PlotManager.createPlot(w, id, playername, uuid);

						//PlotManager.adjustLinkedPlots(id, w);

						if (plot == null) {
							Send(p, caption("ErrCreatingPlotAt") + " " + id);
						} else {
							if (playername.equalsIgnoreCase(p.getName())) {
								Send(p, caption("MsgThisPlotYours") + " Use /plotme " + caption("CommandHome") + " " + caption("MsgToGetToIt") + " " + f(-price));
							} else {
								Send(p, caption("MsgThisPlotIsNow") + " " + playername + caption("WordPossessive") + ". Use /plotme " + caption("CommandHome") + " " + caption("MsgToGetToIt") + " " + f(-price));
							}

							if (price != 0) {
								logger.info(prefixe + playername + " " + caption("MsgClaimedPlot") + " " + id + (" for " + price));
							} else {
								logger.info(prefixe + playername + " " + caption("MsgClaimedPlot") + " " + id + "");
							}
						}
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean home(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.home") || p.hasPermission("PlotMe.admin.home.other")) {
			if (!PlotManager.isPlotWorld(p) && !allowWorldTeleport) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String playername = p.getName();
				UUID uuid = p.getUniqueId();
				int nb = 1;
				World w;
				String worldname = "";

				if (!PlotManager.isPlotWorld(p) && allowWorldTeleport) {
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
							Send(p, "Usage: /plotme " + caption("CommandHome") + ":# " +
									"Example: /plotme " + caption("CommandHome") + ":1");
							return true;
						}
						nb = Integer.parseInt(args[0].split(":")[1]);
					} catch (NumberFormatException ex) {
						Send(p, "Usage: /plotme " + caption("CommandHome") + ":# " +
								"Example: /plotme " + caption("CommandHome") + ":1");
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
						Send(p, args[2] + " does not exist or is not a plot world.");
						return true;
					}
					w = Bukkit.getWorld(args[2]);
					worldname = args[2];
				}

				if (!PlotManager.isPlotWorld(w)) {
					Send(p, worldname + " does not exist or is not a plot world.");
				} else {
					int i = nb - 1;

					for (Plot plot : PlotManager.getPlots(w).values()) {
						if (uuid == null && plot.owner.equalsIgnoreCase(playername) || uuid != null && plot.ownerId != null && plot.ownerId.equals(uuid)) {
							if (i == 0) {
								PlotMapInfo pmi = PlotManager.getMap(w);

								double price = 0;

								if (PlotManager.isEconomyEnabled(w)) {
									price = pmi.PlotHomePrice;
									double balance = economy.getBalance(playername);

									if (balance >= price) {
										EconomyResponse er = economy.withdrawPlayer(playername, price);

										if (!er.transactionSuccess()) {
											Send(p, er.errorMessage);
											return true;
										}
									} else {
										Send(p, caption("MsgNotEnoughTp") + " " + caption("WordMissing") + " " + f(price - balance, false));
										return true;
									}
								}

								p.teleport(PlotManager.getPlotHome(w, plot));

								if (price != 0) {
									Send(p, f(-price));
								}

								return true;
							}
							i--;
						}
					}

					if (nb > 0) {
						if (!playername.equalsIgnoreCase(p.getName())) {
							Send(p, playername + " " + caption("MsgDoesNotHavePlot") + " #" + nb);
						} else {
							Send(p, caption("MsgPlotNotFound") + " #" + nb);
						}
					} else if (!playername.equalsIgnoreCase(p.getName())) {
						Send(p, playername + " " + caption("MsgDoesNotHavePlot"));
					} else {
						Send(p, caption("MsgYouHaveNoPlot"));
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean info(Player p) {
		if (p.hasPermission("PlotMe.use.info")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					Plot plot = PlotManager.getPlotById(p, id);

					p.sendMessage(caption("InfoId") + ": " + id + " " + caption("InfoOwner") + ": " + plot.owner +
							" " + caption("InfoBiome") + ": " + FormatBiome(plot.biome.name()));

					if (plot.finished) {
						if (plot.protect) {
							if (plot.expireddate == null) {
								p.sendMessage(caption("InfoExpire") + ": " + caption("WordNever") +
										" " + caption("InfoFinished") + ": Yes" +
										" " + caption("InfoProtected") + ": Yes");
							} else {
								p.sendMessage(caption("InfoExpire") + ": " + plot.expireddate +
										" " + caption("InfoFinished") + ": Yes" +
										" " + caption("InfoProtected") + ": Yes");
							}
						} else {
							if (plot.expireddate == null) {
								p.sendMessage(caption("InfoExpire") + ": " + caption("WordNever") +
										" " + caption("InfoFinished") + ": Yes" +
										" " + caption("InfoProtected") + ": No");
							} else {
								p.sendMessage(caption("InfoExpire") + ": " + plot.expireddate +
										" " + caption("InfoFinished") + ": Yes" +
										" " + caption("InfoProtected") + ": No");
							}
						}
					} else {
						if (plot.expireddate == null) {
							if (plot.protect) {
								p.sendMessage(caption("InfoExpire") + ": " + caption("WordNever") +
										" " + caption("InfoFinished") + ": No" +
										" " + caption("InfoProtected") + ": Yes");
							} else {
								p.sendMessage(caption("InfoExpire") + ": " + caption("WordNever") +
										" " + caption("InfoFinished") + ": No" +
										" " + caption("InfoProtected") + ": No");
							}
						} else {
							if (plot.protect) {
								p.sendMessage(caption("InfoExpire") + ": " + plot.expireddate +
										" " + caption("InfoFinished") + ": No" +
										" " + caption("InfoProtected") + ": Yes");
							} else {
								p.sendMessage(caption("InfoExpire") + ": " + plot.expireddate +
										" " + caption("InfoFinished") + ": No" +
										" " + caption("InfoProtected") + ": No");
							}
						}
					}

					if (plot.allowedcount() > 0) {
						p.sendMessage(caption("InfoHelpers") + ": " + plot.getAllowed());
					}

					if (allowToDeny && plot.deniedcount() > 0) {
						p.sendMessage(caption("InfoDenied") + ": " + plot.getDenied());
					}

					if (PlotManager.isEconomyEnabled(p)) {
						if (plot.currentbidder == null || plot.currentbidder.equalsIgnoreCase("")) {
							if (plot.forsale) {
								if (plot.auctionned) {
									p.sendMessage(caption("InfoAuctionned") + ": " + ("Yes" +
											" " + caption("InfoMinimumBid") + ": " + round(plot.currentbid)) +
											" " + caption("InfoForSale") + ": " + round(plot.customprice));
								} else {
									p.sendMessage(caption("InfoAuctionned") + ": " + "No" +
											" " + caption("InfoForSale") + ": " + round(plot.customprice));
								}
							} else {
								if (plot.auctionned) {
									p.sendMessage(caption("InfoAuctionned") + ": " + ("Yes" +
											" " + caption("InfoMinimumBid") + ": " + round(plot.currentbid)) +
											" " + caption("InfoForSale") + ": " + "No");
								} else {
									p.sendMessage(caption("InfoAuctionned") + ": " + "No" +
											" " + caption("InfoForSale") + ": " + "No");
								}
							}
						} else {
							if (plot.forsale) {
								if (plot.auctionned) {
									p.sendMessage(caption("InfoAuctionned") + ": " + ("Yes" +
											" " + caption("InfoBidder") + ": " + plot.currentbidder +
											" " + caption("InfoBid") + ": " + round(plot.currentbid)) +
											" " + caption("InfoForSale") + ": " + (round(plot.customprice)));
								} else {
									p.sendMessage(caption("InfoAuctionned") + ": No" +
											" " + caption("InfoForSale") + ": " + (round(plot.customprice)));
								}
							} else {
								if (plot.auctionned) {
									p.sendMessage(caption("InfoAuctionned") + ": " + ("Yes" +
											" " + caption("InfoBidder") + ": " + plot.currentbidder +
											" " + caption("InfoBid") + ": " + round(plot.currentbid)) +
											" " + caption("InfoForSale") + ": No");
								} else {
									p.sendMessage(caption("InfoAuctionned") + ": No " + caption("InfoForSale") + ": No");
								}
							}
						}
					}
				} else {
					Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean comment(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.comment")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else if (args.length < 2) {
				Send(p, "Usage: /plotme " + caption("CommandComment") + " <" + caption("WordText") + ">");
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					World w = p.getWorld();
					PlotMapInfo pmi = PlotManager.getMap(w);
					String playername = p.getName();
					UUID uuid = p.getUniqueId();

					double price = 0;

					if (PlotManager.isEconomyEnabled(w)) {
						price = pmi.AddCommentPrice;
						double balance = economy.getBalance(playername);

						if (balance >= price) {
							EconomyResponse er = economy.withdrawPlayer(playername, price);

							if (!er.transactionSuccess()) {
								Send(p, er.errorMessage);
								warn(er.errorMessage);
								return true;
							}
						} else {
							Send(p, caption("MsgNotEnoughComment") + " " + caption("WordMissing") + " " + f(price - balance, false));
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

					Send(p, caption("MsgCommentAdded") + " " + f(-price));

					if (price != 0) {
						logger.info(prefixe + playername + " " + caption("MsgCommentedPlot") + " " + id + (" for " + price));
					} else {
						logger.info(prefixe + playername + " " + caption("MsgCommentedPlot") + " " + id);
					}
				} else {
					Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean comments(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.comments")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else if (args.length < 2) {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					Plot plot = PlotManager.getPlotById(p, id);

					if (plot.ownerId.equals(p.getUniqueId()) || plot.isAllowed(p.getUniqueId()) || p.hasPermission("PlotMe.admin")) {
						if (plot.comments.isEmpty()) {
							Send(p, caption("MsgNoComments"));
						} else {
							Send(p, caption("MsgYouHave") + " " + plot.comments.size() + " " + caption("MsgComments"));

							for (String[] comment : plot.comments) {
								p.sendMessage(caption("WordFrom") + " : " + comment[0]);
								p.sendMessage(comment[1]);
							}

						}
					} else {
						Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgNotYoursNotAllowedViewComments"));
					}
				} else {
					Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
				}
			}
		} else {
			p.sendMessage(PREFIX + caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean biome(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.biome")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					p.sendMessage(PREFIX + caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					World w = p.getWorld();

					if (args.length == 2) {
						Biome biome = null;

						for (Biome bio : Biome.values()) {
							if (bio.name().equalsIgnoreCase(args[1])) {
								biome = bio;
							}
						}

						if (biome == null) {
							Send(p, args[1] + " " + caption("MsgIsInvalidBiome"));
						} else {
							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();

							if (plot.owner.equalsIgnoreCase(playername) || p.hasPermission("PlotMe.admin")) {
								PlotMapInfo pmi = PlotManager.getMap(w);

								double price = 0;

								if (PlotManager.isEconomyEnabled(w)) {
									price = pmi.BiomeChangePrice;
									double balance = economy.getBalance(playername);

									if (balance >= price) {
										EconomyResponse er = economy.withdrawPlayer(playername, price);

										if (!er.transactionSuccess()) {
											Send(p, er.errorMessage);
											warn(er.errorMessage);
											return true;
										}
									} else {
										Send(p, caption("MsgNotEnoughBiome") + " " + caption("WordMissing") + " " + f(price - balance, false));
										return true;
									}
								}

								PlotManager.setBiome(w, id, plot, biome);

								Send(p, caption("MsgBiomeSet") + " " + FormatBiome(biome.name()) + " " + f(-price));

								if (price != 0) {
									logger.info(prefixe + playername + " " + caption("MsgChangedBiome") + " " + id + " " + caption("WordTo") + " " +
											FormatBiome(biome.name()) + (" for " + price));
								} else {
									logger.info(prefixe + playername + " " + caption("MsgChangedBiome") + " " + id + " " + caption("WordTo") + " " + FormatBiome(biome.name()));
								}
							} else {
								Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgNotYoursNotAllowedBiome"));
							}
						}
					} else {
						Plot plot = plotmaps.get(w.getName().toLowerCase()).plots.get(id);

						Send(p, caption("MsgPlotUsingBiome") + " " + FormatBiome(plot.biome.name()));
					}
				} else {
					Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean biomelist(CommandSender s) {
		if (!(s instanceof Player) || s.hasPermission("PlotMe.use.biome")) {
			Send(s, caption("WordBiomes") + " : ");

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

				s.sendMessage(String.valueOf(line));
				//i = 0;
				line = new StringBuilder();
								
				/*int nameLength = MinecraftFontWidthCalculator.getStringWidth(b);
				
				i += 1;
				if(i == 3)
				{
					line.append(b);
					s.sendMessage("" + line);
					i = 0;
					line = new StringBuilder();
				}
				else
				{
					line.append(b).append(whitespace(318 - nameLength));
				}*/
			}
		} else {
			Send(s, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean reset(Player p) {
		if (p.hasPermission("PlotMe.admin.reset")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				Plot plot = PlotManager.getPlotById(p.getLocation());

				if (plot == null) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (plot.protect) {
					Send(p, caption("MsgPlotProtectedCannotReset"));
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
								EconomyResponse er = economy.depositPlayer(currentbidder, plot.currentbid);

								if (!er.transactionSuccess()) {
									Send(p, er.errorMessage);
									warn(er.errorMessage);
								} else {
									for (Player player : Bukkit.getServer().getOnlinePlayers()) {
										if (player.getName().equalsIgnoreCase(currentbidder)) {
											Send(player, "Spot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgWasReset") + " " + f(plot.currentbid));
											break;
										}
									}
								}
							}
						}

						PlotMapInfo pmi = PlotManager.getMap(p);

						if (pmi.RefundClaimPriceOnReset) {
							EconomyResponse er = economy.depositPlayer(plot.owner, pmi.ClaimPrice);

							if (!er.transactionSuccess()) {
								Send(p, er.errorMessage);
								warn(er.errorMessage);
								return true;
							}
							for (Player player : Bukkit.getServer().getOnlinePlayers()) {
								if (player.getName().equalsIgnoreCase(plot.owner)) {
									Send(player, "Spot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgWasReset") + " " + f(pmi.ClaimPrice));
									break;
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

					Send(p, caption("MsgPlotReset"));

					logger.info(prefixe + name + " " + caption("MsgResetPlot") + " " + id);
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean clear(Player p) {
		if (p.hasPermission("PlotMe.admin.clear") || p.hasPermission("PlotMe.use.clear")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					Plot plot = PlotManager.getPlotById(p, id);

					if (plot.protect) {
						Send(p, caption("MsgPlotProtectedCannotClear"));
					} else {
						String playername = p.getName();

						if (plot.owner.equalsIgnoreCase(playername) || p.hasPermission("PlotMe.admin.clear")) {
							World w = p.getWorld();

							PlotMapInfo pmi = PlotManager.getMap(w);

							double price = 0;

							if (PlotManager.isEconomyEnabled(w)) {
								price = pmi.ClearPrice;
								double balance = economy.getBalance(playername);

								if (balance >= price) {
									EconomyResponse er = economy.withdrawPlayer(playername, price);

									if (!er.transactionSuccess()) {
										Send(p, er.errorMessage);
										warn(er.errorMessage);
										return true;
									}
								} else {
									Send(p, caption("MsgNotEnoughClear") + " " + caption("WordMissing") + " " + (price - balance) + " " + economy.currencyNamePlural());
									return true;
								}
							}

							PlotManager.clear(w, plot);
							//RemoveLWC(w, plot, p);
							//PlotManager.regen(w, plot);

							Send(p, caption("MsgPlotCleared") + " " + f(-price));

							if (price != 0) {
								logger.info(prefixe + playername + " " + caption("MsgClearedPlot") + " " + id + (" for " + price));
							} else {
								logger.info(prefixe + playername + " " + caption("MsgClearedPlot") + " " + id);
							}
						} else {
							Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgNotYoursNotAllowedClear"));
						}
					}
				} else {
					Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean add(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.add") || p.hasPermission("PlotMe.use.add")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					if (args.length < 2 || args[1].equalsIgnoreCase("")) {
						Send(p, "Usage /plotme " + caption("CommandAdd") + " <" + caption("WordPlayer") + ">");
					} else {

						Plot plot = PlotManager.getPlotById(p, id);
						String playername = p.getName();
						String allowed = args[1];

						if (plot.owner.equalsIgnoreCase(playername) || p.hasPermission("PlotMe.admin.add")) {
							if (plot.isAllowedConsulting(allowed) || plot.isGroupAllowed(allowed)) {
								Send(p, caption("WordPlayer") + " " + args[1] + " " + caption("MsgAlreadyAllowed"));
							} else {
								World w = p.getWorld();

								PlotMapInfo pmi = PlotManager.getMap(w);

								double price = 0;

								if (PlotManager.isEconomyEnabled(w)) {
									price = pmi.AddPlayerPrice;
									double balance = economy.getBalance(playername);

									if (balance >= price) {
										EconomyResponse er = economy.withdrawPlayer(playername, price);

										if (!er.transactionSuccess()) {
											Send(p, er.errorMessage);
											warn(er.errorMessage);
											return true;
										}
									} else {
										Send(p, caption("MsgNotEnoughAdd") + " " + caption("WordMissing") + " " + f(price - balance, false));
										return true;
									}
								}

								plot.addAllowed(allowed);
								plot.removeDenied(allowed);

								Send(p, caption("WordPlayer") + " " + allowed + " " + caption("MsgNowAllowed") + " " + f(-price));

								if (price != 0) {
									logger.info(prefixe + playername + " " + caption("MsgAddedPlayer") + " " + allowed + " " + caption("MsgToPlot") + " " + id + (" for " + price));
								} else {
									logger.info(prefixe + playername + " " + caption("MsgAddedPlayer") + " " + allowed + " " + caption("MsgToPlot") + " " + id);
								}
							}
						} else {
							Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgNotYoursNotAllowedAdd"));
						}
					}
				} else {
					Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean deny(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.deny") || p.hasPermission("PlotMe.use.deny")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					if (args.length < 2 || args[1].equalsIgnoreCase("")) {
						Send(p, "Usage /plotme " + caption("CommandDeny") + " <" + caption("WordPlayer") + ">");
					} else {

						Plot plot = PlotManager.getPlotById(p, id);
						String playername = p.getName();
						String denied = args[1];

						if (plot.owner.equalsIgnoreCase(playername) || p.hasPermission("PlotMe.admin.deny")) {
							if (plot.isDeniedConsulting(denied) || plot.isGroupDenied(denied)) {
								Send(p, caption("WordPlayer") + " " + args[1] + " " + caption("MsgAlreadyDenied"));
							} else {
								World w = p.getWorld();

								PlotMapInfo pmi = PlotManager.getMap(w);

								double price = 0;

								if (PlotManager.isEconomyEnabled(w)) {
									price = pmi.DenyPlayerPrice;
									double balance = economy.getBalance(playername);

									if (balance >= price) {
										EconomyResponse er = economy.withdrawPlayer(playername, price);

										if (!er.transactionSuccess()) {
											Send(p, er.errorMessage);
											warn(er.errorMessage);
											return true;
										}
									} else {
										Send(p, caption("MsgNotEnoughDeny") + " " + caption("WordMissing") + " " + f(price - balance, false));
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

								Send(p, caption("WordPlayer") + " " + denied + " " + caption("MsgNowDenied") + " " + f(-price));

								if (price != 0) {
									logger.info(prefixe + playername + " " + caption("MsgDeniedPlayer") + " " + denied + " " + caption("MsgToPlot") + " " + id + (" for " + price));
								} else {
									logger.info(prefixe + playername + " " + caption("MsgDeniedPlayer") + " " + denied + " " + caption("MsgToPlot") + " " + id);
								}
							}
						} else {
							Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgNotYoursNotAllowedDeny"));
						}
					}
				} else {
					Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean remove(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.remove") || p.hasPermission("PlotMe.use.remove")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					if (args.length < 2 || args[1].equalsIgnoreCase("")) {
						Send(p, "Usage: /plotme " + caption("CommandRemove") + " <" + caption("WordPlayer") + ">");
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
									double balance = economy.getBalance(playername);

									if (balance >= price) {
										EconomyResponse er = economy.withdrawPlayer(playername, price);

										if (!er.transactionSuccess()) {
											Send(p, er.errorMessage);
											warn(er.errorMessage);
											return true;
										}
									} else {
										Send(p, caption("MsgNotEnoughRemove") + " " + caption("WordMissing") + " " + f(price - balance, false));
										return true;
									}
								}

								if (allowed.startsWith("group:")) {
									plot.removeAllowedGroup(allowed);
								} else {
									plot.removeAllowed(allowed);
								}

								Send(p, caption("WordPlayer") + " " + allowed + " " + caption("WordRemoved") + ". " + f(-price));

								if (price != 0) {
									logger.info(prefixe + p.getName() + " " + caption("MsgRemovedPlayer") + " " + allowed + " " + caption("MsgFromPlot") + " " + id + (" for " + price));
								} else {
									logger.info(prefixe + p.getName() + " " + caption("MsgRemovedPlayer") + " " + allowed + " " + caption("MsgFromPlot") + " " + id);
								}
							} else {
								Send(p, caption("WordPlayer") + " " + args[1] + " " + caption("MsgWasNotAllowed"));
							}
						} else {
							Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgNotYoursNotAllowedRemove"));
						}
					}
				} else {
					Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean undeny(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.undeny") || p.hasPermission("PlotMe.use.undeny")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							Send(p, "Usage: /plotme " + caption("CommandUndeny") + " <" + caption("WordPlayer") + ">");
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
										double balance = economy.getBalance(playername);

										if (balance >= price) {
											EconomyResponse er = economy.withdrawPlayer(playername, price);

											if (!er.transactionSuccess()) {
												Send(p, er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											Send(p, caption("MsgNotEnoughUndeny") + " " + caption("WordMissing") + " " + f(price - balance, false));
											return true;
										}
									}

									if (denied.startsWith("group:")) {
										plot.removeDeniedGroup(denied);
									} else {
										plot.removeDenied(denied);
									}

									Send(p, caption("WordPlayer") + " " + denied + " " + caption("MsgNowUndenied") + " " + f(-price));

									if (price != 0) {
										logger.info(prefixe + playername + " " + caption("MsgUndeniedPlayer") + " " + denied + " " + caption("MsgFromPlot") + " " + id + (" for " + price));
									} else {
										logger.info(prefixe + playername + " " + caption("MsgUndeniedPlayer") + " " + denied + " " + caption("MsgFromPlot") + " " + id);
									}
								} else {
									Send(p, caption("WordPlayer") + " " + args[1] + " " + caption("MsgWasNotDenied"));
								}
							} else {
								Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgNotYoursNotAllowedUndeny"));
							}
						}
					} else {
						Send(p, caption("MsgThisPlot") + "(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean setowner(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.setowner")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else if (args.length < 2 || args[1].isEmpty()) {
					Send(p, "Usage: /plotme " + caption("CommandSetowner") + " <" + caption("WordPlayer") + ">");
				} else {
					String newowner = args[1];
					String oldowner = "<N/A>";
					String playername = p.getName();

					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						PlotMapInfo pmi = PlotManager.getMap(p);
						oldowner = plot.owner;

						if (PlotManager.isEconomyEnabled(p)) {
							if (pmi.RefundClaimPriceOnSetOwner && !newowner.equals(oldowner)) {
								EconomyResponse er = economy.depositPlayer(oldowner, pmi.ClaimPrice);

								if (!er.transactionSuccess()) {
									Send(p, er.errorMessage);
									warn(er.errorMessage);
									return true;
								}
								for (Player player : Bukkit.getServer().getOnlinePlayers()) {
									if (player.getName().equalsIgnoreCase(oldowner)) {
										Send(player, caption("MsgYourPlot") + " " + id + " " + caption("MsgNowOwnedBy") + " " + newowner + ". " + f(pmi.ClaimPrice));
										break;
									}
								}
							}

							if (plot.currentbidder != null && !plot.currentbidder.isEmpty()) {
								EconomyResponse er = economy.depositPlayer(plot.currentbidder, plot.currentbid);

								if (!er.transactionSuccess()) {
									Send(p, er.errorMessage);
									warn(er.errorMessage);
								} else {
									for (Player player : Bukkit.getServer().getOnlinePlayers()) {
										if (player.getName().equalsIgnoreCase(plot.currentbidder)) {
											Send(player, "Spot " + id + " " + caption("MsgChangedOwnerFrom") + " " + oldowner + " " + caption("WordTo") + " " + newowner + ". " + f(plot.currentbid));
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

					Send(p, caption("MsgOwnerChangedTo") + " " + newowner);

					logger.info(prefixe + playername + " " + caption("MsgChangedOwnerOf") + " " + id + " " + caption("WordFrom") + " " + oldowner + " " + caption("WordTo") + " " + newowner);
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean id(Player p) {
		if (p.hasPermission("PlotMe.admin.id")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String plotid = PlotManager.getPlotId(p.getLocation());

				if (plotid.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else {
					p.sendMessage("Spot id: " + plotid);

					Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), plotid);
					Location top = PlotManager.getPlotTopLoc(p.getWorld(), plotid);

					p.sendMessage(caption("WordBottom") + ": " + bottom.getBlockX() + "," + bottom.getBlockZ());
					p.sendMessage(caption("WordTop") + ": " + top.getBlockX() + "," + top.getBlockZ());
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	/*private boolean move(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.move")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, C("MsgNotPlotWorld"));
			} else if (args.length < 3 || args[1].equalsIgnoreCase("") || args[2].equalsIgnoreCase("")) {
				Send(p, C("WordUsage") + ": /plotme " + C("CommandMove") + " <" + C("WordIdFrom") + "> <" + C("WordIdTo") + "> " +
						C("WordExample") + ": /plotme " + C("CommandMove") + " 0;1 2;-1");
			} else {
				String plot1 = args[1];
				String plot2 = args[2];

				if (!PlotManager.isValidId(plot1) || !PlotManager.isValidId(plot2)) {
					Send(p, C("WordUsage") + ": /plotme " + C("CommandMove") + " <" + C("WordIdFrom") + "> <" + C("WordIdTo") + "> " +
							C("WordExample") + ": /plotme " + C("CommandMove") + " 0;1 2;-1");
					return true;
				}
				if (PlotManager.movePlot(p.getWorld(), plot1, plot2)) {
					Send(p, C("MsgPlotMovedSuccess"));

					PlotMe.logger.info(prefixe + p.getName() + " " + C("MsgExchangedPlot") + " " + plot1 + " " + C("MsgAndPlot") + " " + plot2);
				} else {
					Send(p, C("ErrMovingPlot"));
				}
			}
		} else {
			Send(p, C("MsgPermissionDenied"));
		}
		return true;
	}*/

	private boolean reload(CommandSender s) {
		if (!(s instanceof Player) || s.hasPermission("PlotMe.admin.reload")) {
			plugin.initialize();
			Send(s, caption("MsgReloadedSuccess"));

			logger.info(prefixe + s.getName() + " " + caption("MsgReloadedConfigurations"));
		} else {
			Send(s, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private CharSequence whitespace(int length) {
		int spaceWidth = MinecraftFontWidthCalculator.getStringWidth(" ");

		StringBuilder ret = new StringBuilder();

		for (int i = 0; (i + spaceWidth) < length; i += spaceWidth) {
			ret.append(" ");
		}

		return ret;
	}

	private String round(double money) {
		if (money % 1 == 0) {
			return String.valueOf(Math.round(money));
		}
		return String.valueOf(money);
	}

	private void warn(String msg) {
		logger.warning(prefixe + msg);
	}

	private String f(double price) {
		return f(price, true);
	}

	private String f(double price, boolean showsign) {
		if (price == 0) {
			return "";
		}

		String format = round(Math.abs(price));

		if (economy != null) {
			if (price <= 1 && price >= -1) {
				format = format + " " + economy.currencyNameSingular();
			} else {
				format = format + " " + economy.currencyNamePlural();
			}
		}

		if (showsign) {
			if (price > 0) {
				return ("+" + format);
			}
			return ("-" + format);
		}
		return format;
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