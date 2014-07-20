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
	private final PlotMe plugin;

	public PMCommand(PlotMe instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("plotme") || label.equalsIgnoreCase("plot") || label.equalsIgnoreCase("p")) {
			if (!(sender instanceof Player)) {
				if (args.length == 0 || args[0].equalsIgnoreCase("1")) {
					sender.sendMessage(caption("ConsoleHelpMain"));
					sender.sendMessage(" - /plotme reload");
					sender.sendMessage(caption("ConsoleHelpReload"));
					return true;
				} else {
					String a0 = args[0];
					if (a0.equalsIgnoreCase("reload")) {
						return reload(sender, args);
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
					} catch (NumberFormatException ignored) {
					}

					if (ipage != -1) {
						return showhelp(p, ipage);
					} else {
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
							} else {
								return showhelp(p, 1);
							}
						}
						if (a0.equalsIgnoreCase(caption("CommandClaim"))) {
							return claim(p, args);
						}
						if (a0.equalsIgnoreCase("auto")) {
							return auto(p, args);
						}
						if (a0.equalsIgnoreCase("info") || a0.equalsIgnoreCase("i")) {
							return info(p);
						}
						if (a0.equalsIgnoreCase(caption("CommandComment"))) {
							return comment(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandComments")) || a0.equalsIgnoreCase("command")) {
							return comments(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandBiome")) || a0.equalsIgnoreCase("b")) {
							return biome(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandBiomelist"))) {
							return biomelist(p, args);
						}
						if (a0.equalsIgnoreCase("id")) {
							return id(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandTp"))) {
							return tp(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandClear"))) {
							return clear(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandReset"))) {
							return reset(p, args);
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
						if (a0.equalsIgnoreCase(caption("CommandMove")) || a0.equalsIgnoreCase("m")) {
							return move(p, args);
						}
						if (a0.equalsIgnoreCase("reload")) {
							return reload(sender, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandList"))) {
							return plotlist(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandExpired"))) {
							return expired(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandAddtime"))) {
							return addtime(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandDone"))) {
							return done(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandDoneList"))) {
							return donelist(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandProtect"))) {
							return protect(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandSell"))) {
							return sell(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandDispose"))) {
							return dispose(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandAuction"))) {
							return auction(p, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandBuy"))) {
							return buy(p);
						}
						if (a0.equalsIgnoreCase("bid")) {
							return bid(p, args);
						}
						if (a0.startsWith("home")) {
							return home(p, args);
						}
						if (a0.equalsIgnoreCase("resetexpired")) {
							return resetexpired(p, args);
						}
					}
				}
			}
		}
		return false;
	}

	private boolean resetexpired(Player player, String[] args) {
		if (player.hasPermission("PlotMe.admin.resetexpired")) {
			if (args.length <= 1) {
				player.sendMessage("Usage: /plotme resetexpired <world>");
				player.sendMessage("Example: /plotme resetexpired plotworld");
			} else {
				if (worldcurrentlyprocessingexpired != null) {
					player.sendMessage(cscurrentlyprocessingexpired.getName() + " "+ caption("MsgAlreadyProcessingPlots"));
				} else {
					World w = player.getServer().getWorld(args[1]);

					if (w == null) {
						player.sendMessage("World \"" + args[1] + "\" " + caption("MsgDoesNotExistOrNotLoaded"));
						return true;
					} else {
						if (!PlotManager.isPlotWorld(w)) {
							player.sendMessage(caption("MsgNotPlotworld"));
							return true;
						} else {
							worldcurrentlyprocessingexpired = w;
							cscurrentlyprocessingexpired = player;
							counterexpired = 50;
							nbperdeletionprocessingexpired = 5;

							plugin.scheduleTask(new PlotRunnableDeleteExpire());
						}
					}
				}
			}
		} else {
			player.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean bid(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			if (p.hasPermission("PlotMe.use.bid")) {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot.auctionned) {
							String bidder = p.getName();

							if (plot.owner.equalsIgnoreCase(bidder)) {
								p.sendMessage(caption("MsgCannotBidOwnPlot"));
							} else {
								if (args.length == 2) {
									double bid = 0;
									double currentbid = plot.currentbid;
									String currentbidder = plot.currentbidder;

									try {
										bid = Double.parseDouble(args[1]);
									} catch (NumberFormatException ignored) {
									}

									if (bid < currentbid || (bid == currentbid && !currentbidder.isEmpty())) {
										p.sendMessage(caption("MsgInvalidBidMustBeAbove") + " " + f(plot.currentbid, false));
									} else {
										double balance = economy.getBalance(bidder);

										if (bid >= balance && !currentbidder.equals(bidder) ||
												currentbidder.equals(bidder) && bid > (balance + currentbid)) {
											p.sendMessage(caption("MsgNotEnoughBid"));
										} else {
											EconomyResponse er = economy.withdrawPlayer(bidder, bid);

											if (er.transactionSuccess()) {
												if (!currentbidder.isEmpty()) {
													EconomyResponse er2 = economy.depositPlayer(currentbidder, currentbid);

													if (!er2.transactionSuccess()) {
														p.sendMessage(er2.errorMessage);
														warn(er2.errorMessage);
													} else {
														for (Player player : Bukkit.getServer().getOnlinePlayers()) {
															if (player.getName().equalsIgnoreCase(currentbidder)) {
																player.sendMessage(caption("MsgOutbidOnPlot") + " " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + ". " + f(bid));
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

												p.sendMessage(caption("MsgBidAccepted") + " " + f(-bid));

												logger.info("[PlotMe Event] " + bidder + " bid " + bid + " on plot " + id);
											} else {
												p.sendMessage(er.errorMessage);
												warn(er.errorMessage);
											}
										}
									}
								} else {
									p.sendMessage("Usage: /plotme bid <amount> ");
									p.sendMessage("Example: /plotme bid 100");
								}
							}
						} else {
							p.sendMessage(caption("MsgPlotNotAuctionned"));
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			} else {
				p.sendMessage(caption("MsgPermissionDenied"));
			}
		} else {
			p.sendMessage(caption("MsgEconomyDisabledWorld"));
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
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (!plot.forsale) {
							p.sendMessage(caption("MsgPlotNotForSale"));
						} else {
							String buyer = p.getName();

							if (plot.owner.equalsIgnoreCase(buyer)) {
								Send(p, caption("MsgCannotBuyOwnPlot"));
							} else {
								int plotlimit = getPlotLimit(p);

								if (plotlimit != -1 && PlotManager.getNbOwnedPlot(p) >= plotlimit) {
									Send(p, caption("MsgAlreadyReachedMaxPlots") + " (" +
											PlotManager.getNbOwnedPlot(p) + "/" + getPlotLimit(p) + "). " +
											"Use /plotme home " + caption("MsgToGetToIt"));
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
															Send(player, "Plot " + id + " " +
																	caption("MsgSoldTo") + " " + buyer + ". " + f(cost));
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

											logger.info("[PlotMe Event] " + buyer + " " + caption("MsgBoughtPlot") + " " + id + " for " + cost);
										} else {
											Send(p, er.errorMessage);
											warn(er.errorMessage);
										}
									}
								}
							}
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
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
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
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

										logger.info("[PlotMe Event] " + name + " " + caption("MsgStoppedTheAuctionOnPlot") + " " + id);
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

										logger.info("[PlotMe Event] " + name + " " + caption("MsgStartedAuctionOnPlot") + " " + id + " at " + bid);
									}
								}
							} else {
								Send(p, caption("MsgDoNotOwnPlot"));
							}
						} else {
							Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
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

	private boolean dispose(Player p, String[] args) {
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
														Send(player, "Plot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgWasDisposed") + " " + f(cost));
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

								logger.info("[PlotMe Event] " + name + " " + caption("MsgDisposedPlot") + " " + id);
							} else {
								Send(p, "This plot(" + id + ") " + caption("MsgNotYoursCannotDispose"));
							}
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
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
				if (p.hasPermission("PlotMe.use.sell") || p.hasPermission("PlotMe.admin.sell")) {
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

									logger.info("[PlotMe Event] " + name + " " + caption("MsgRemovedPlot") + " " + id + " " + caption("MsgFromBeingSold"));
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
														p.sendMessage("Usage: /plotme " + caption("CommandSellBank") + "|<amount>");
														p.sendMessage("Example: /plotme " + caption("CommandSellBank") + "  or  /plotme " + caption("CommandSell") + " 200");
													} else {
														p.sendMessage("Usage: /plotme " + caption("CommandSell") + " <amount>");
														p.sendMessage("Example: /plotme " + caption("CommandSell") + " 200");
													}
												}
											} else {
												p.sendMessage(caption("MsgCannotCustomPriceDefault") + " " + price);
												return true;
											}
										}
									}

									if (bank) {
										if (!pmi.CanSellToBank) {
											p.sendMessage(caption("MsgCannotSellToBank"));
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
															Send(player, "Plot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgSoldToBank") + " " + f(bid));
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

												logger.info("[PlotMe Event] " + name + " " + caption("MsgSoldToBankPlot") + " " + id + " for " + sellprice);
											} else {
												Send(p, " " + er.errorMessage);
												warn(er.errorMessage);
											}
										}
									} else {
										if (price < 0) {
											Send(p, caption("MsgInvalidAmount"));
										} else {
											plot.customprice = price;
											plot.forsale = true;

											plot.updateField("customprice", price);
											plot.updateField("forsale", true);

											PlotManager.adjustWall(l);
											PlotManager.setSellSign(w, plot);

											Send(p, caption("MsgPlotForSale"));

											logger.info("[PlotMe Event] " + name + " " + caption("MsgPutOnSalePlot") + " " + id + " for " + price);
										}
									}
								}
							} else {
								Send(p, caption("MsgDoNotOwnPlot"));
							}
						} else {
							Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
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

	private boolean protect(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.protect") || p.hasPermission("PlotMe.use.protect")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						String name = p.getName();

						if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.protect")) {
							if (plot.protect) {
								plot.protect = false;
								PlotManager.adjustWall(p.getLocation());

								plot.updateField("protected", false);

								Send(p, caption("MsgPlotNoLongerProtected"));

								logger.info("[PlotMe Event] " + name + " " + caption("MsgUnprotectedPlot") + " " + id);
							} else {
								PlotMapInfo pmi = PlotManager.getMap(p);

								double cost = 0;

								if (PlotManager.isEconomyEnabled(p)) {
									cost = pmi.ProtectPrice;

									if (economy.getBalance(name) < cost) {
										Send(p, caption("MsgNotEnoughProtectPlot"));
										return true;
									} else {
										EconomyResponse er = economy.withdrawPlayer(name, cost);

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

								Send(p, caption("MsgPlotNowProtected") + " " + f(-cost));

								logger.info("[PlotMe Event] " + name + " " + caption("MsgProtectedPlot") + " " + id);

							}
						} else {
							Send(p, caption("MsgDoNotOwnPlot"));
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
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
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean done(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.done") || p.hasPermission("PlotMe.admin.done")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);
						String name = p.getName();

						if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.done")) {
							if (plot.finished) {
								plot.setUnfinished();
								Send(p, caption("MsgUnmarkFinished"));

								logger.info("[PlotMe Event] " + name + " " + caption("WordMarked") + " " + id + " " + caption("WordFinished"));
							} else {
								plot.setFinished();
								Send(p, caption("MsgMarkFinished"));

								logger.info("[PlotMe Event] " + name + " " + caption("WordMarked") + " " + id + " " + caption("WordUnfinished"));
							}
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean addtime(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.addtime")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot != null) {
							String name = p.getName();

							plot.resetExpire(PlotManager.getMap(p).DaysToExpiration);
							Send(p, caption("MsgPlotExpirationReset"));

							logger.info("[PlotMe Event] " + name + " reset expiration on plot " + id);
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
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
			} else {
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

				int maxpage = (int) Math.ceil(((double) nbexpiredplots / (double) pagesize));

				if (expiredplots.isEmpty()) {
					Send(p, caption("MsgNoPlotExpired"));
				} else {
					Send(p, caption("MsgExpiredPlotsPage") + " " + page + "/" + maxpage);

					for (int i = (page - 1) * pagesize; i < expiredplots.size() && i < (page * pagesize); i++) {
						Plot plot = expiredplots.get(i);

						String starttext = "  " + plot.id + " -> " + plot.owner;

						int textLength = MinecraftFontWidthCalculator.getStringWidth(starttext);

						String line = starttext + whitespace(550 - textLength) + "@" + plot.expireddate;

						p.sendMessage(line);
					}
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
			} else {
				String name;
				String pname = p.getName();

				if (p.hasPermission("PlotMe.admin.list") && args.length == 2) {
					name = args[1];
					p.sendMessage(caption("MsgListOfPlotsWhere") + " " + name + " " + caption("MsgCanBuild"));
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
						addition.append(" Auction: " + round(plot.currentbid) + ((plot.currentbidder != null && !plot.currentbidder.isEmpty()) ? " " + plot.currentbidder : ""));
					}

					if (plot.forsale) {
						addition.append(" Sell: " + round(plot.customprice));
					}

					if (plot.owner.equalsIgnoreCase(name)) {
						if (plot.allowedcount() == 0) {
							if (name.equalsIgnoreCase(pname)) {
								p.sendMessage("  " + plot.id + " -> Yours" + addition);
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
								p.sendMessage("  " + plot.id + " -> Yours" + addition + ", " + caption("WordHelpers") + ": " + helpers);
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
							p.sendMessage("  " + plot.id + " -> Yours" + addition + ", " + caption("WordHelpers") + ": " + helpers);
						} else {
							p.sendMessage("  " + plot.id + " -> " + plot.owner + caption("WordPossessive") + addition + ", " + caption("WordHelpers") + ": " + helpers);
						}
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean showhelp(Player p, int page) {
		int max = 4;
		int maxpage;
		boolean ecoon = PlotManager.isEconomyEnabled(p);

		List<String> allowed_commands = new ArrayList<>();

		allowed_commands.add("limit");
		if (p.hasPermission("PlotMe.use.claim")) {
			allowed_commands.add("claim");
		}
		if (p.hasPermission("PlotMe.use.claim.other")) {
			allowed_commands.add("claim.other");
		}
		if (p.hasPermission("PlotMe.use.auto")) {
			allowed_commands.add("auto");
		}
		if (p.hasPermission("PlotMe.use.home")) {
			allowed_commands.add("home");
		}
		if (p.hasPermission("PlotMe.use.home.other")) {
			allowed_commands.add("home.other");
		}
		if (p.hasPermission("PlotMe.use.info")) {
			allowed_commands.add("info");
			allowed_commands.add("biomeinfo");
		}
		if (p.hasPermission("PlotMe.use.comment")) {
			allowed_commands.add("comment");
		}
		if (p.hasPermission("PlotMe.use.comments")) {
			allowed_commands.add("comments");
		}
		if (p.hasPermission("PlotMe.use.list")) {
			allowed_commands.add("list");
		}
		if (p.hasPermission("PlotMe.use.biome")) {
			allowed_commands.add("biome");
			allowed_commands.add("biomelist");
		}
		if (p.hasPermission("PlotMe.use.done") ||
				p.hasPermission("PlotMe.admin.done")) {
			allowed_commands.add("done");
		}
		if (p.hasPermission("PlotMe.admin.done")) {
			allowed_commands.add("donelist");
		}
		if (p.hasPermission("PlotMe.admin.tp")) {
			allowed_commands.add("tp");
		}
		if (p.hasPermission("PlotMe.admin.id")) {
			allowed_commands.add("id");
		}
		if (p.hasPermission("PlotMe.use.clear") ||
				p.hasPermission("PlotMe.admin.clear")) {
			allowed_commands.add("clear");
		}
		if (p.hasPermission("PlotMe.admin.dispose") ||
				p.hasPermission("PlotMe.use.dispose")) {
			allowed_commands.add("dispose");
		}
		if (p.hasPermission("PlotMe.admin.reset")) {
			allowed_commands.add("reset");
		}
		if (p.hasPermission("PlotMe.use.add") ||
				p.hasPermission("PlotMe.admin.add")) {
			allowed_commands.add("add");
		}
		if (p.hasPermission("PlotMe.use.remove") ||
				p.hasPermission("PlotMe.admin.remove")) {
			allowed_commands.add("remove");
		}
		if (allowToDeny) {
			if (p.hasPermission("PlotMe.use.deny") ||
					p.hasPermission("PlotMe.admin.deny")) {
				allowed_commands.add("deny");
			}
			if (p.hasPermission("PlotMe.use.undeny") ||
					p.hasPermission("PlotMe.admin.undeny")) {
				allowed_commands.add("undeny");
			}
		}
		if (p.hasPermission("PlotMe.admin.setowner")) {
			allowed_commands.add("setowner");
		}
		if (p.hasPermission("PlotMe.admin.move")) {
			allowed_commands.add("move");
		}
		if (p.hasPermission("PlotMe.admin.reload")) {
			allowed_commands.add("reload");
		}
		if (p.hasPermission("PlotMe.admin.list")) {
			allowed_commands.add("listother");
		}
		if (p.hasPermission("PlotMe.admin.expired")) {
			allowed_commands.add("expired");
		}
		if (p.hasPermission("PlotMe.admin.addtime")) {
			allowed_commands.add("addtime");
		}
		if (p.hasPermission("PlotMe.admin.resetexpired")) {
			allowed_commands.add("resetexpired");
		}

		PlotMapInfo pmi = PlotManager.getMap(p);

		if (PlotManager.isPlotWorld(p) && ecoon) {
			if (p.hasPermission("PlotMe.use.buy")) {
				allowed_commands.add("buy");
			}
			if (p.hasPermission("PlotMe.use.sell")) {
				allowed_commands.add("sell");
				if (pmi.CanSellToBank) {
					allowed_commands.add("sellbank");
				}
			}
			if (p.hasPermission("PlotMe.use.auction")) {
				allowed_commands.add("auction");
			}
			if (p.hasPermission("PlotMe.use.bid")) {
				allowed_commands.add("bid");
			}
		}

		maxpage = (int) Math.ceil((double) allowed_commands.size() / max);

		if (page > maxpage) {
			page = 1;
		}

		p.sendMessage(" ---==" + caption("HelpTitle") + " " + page + "/" + maxpage + "==--- ");

		for (int ctr = (page - 1) * max; ctr < (page * max) && ctr < allowed_commands.size(); ctr++) {
			String allowedcmd = allowed_commands.get(ctr);

			if (allowedcmd.equalsIgnoreCase("limit")) {
				if (PlotManager.isPlotWorld(p) || allowWorldTeleport) {
					World w = null;

					if (PlotManager.isPlotWorld(p)) {
						w = p.getWorld();
					} else if (allowWorldTeleport) {
						w = PlotManager.getFirstWorld();
					}

					int maxplots = getPlotLimit(p);
					int ownedplots = PlotManager.getNbOwnedPlot(p, w);

					if (maxplots == -1) {
						p.sendMessage(caption("HelpYourPlotLimitWorld") + " : " + ownedplots + " " + caption("HelpUsedOf") + " " + caption("WordInfinite"));
					} else {
						p.sendMessage(caption("HelpYourPlotLimitWorld") + " : " + ownedplots + " " + caption("HelpUsedOf") + " " + maxplots);
					}
				} else {
					p.sendMessage(caption("HelpYourPlotLimitWorld") + " : " + caption("MsgNotPlotWorld"));
				}
			} else if (allowedcmd.equalsIgnoreCase("claim")) {
				p.sendMessage(" /plotme " + caption("CommandClaim"));
				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					p.sendMessage(" " + caption("HelpClaim") + " " + caption("WordPrice") + " : " + round(pmi.ClaimPrice));
				} else {
					p.sendMessage(" " + caption("HelpClaim"));
				}
			} else if (allowedcmd.equalsIgnoreCase("claim.other")) {
				p.sendMessage(" /plotme " + caption("CommandClaim") + " <" + caption("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					p.sendMessage(" " + caption("HelpClaimOther") + " " + caption("WordPrice") + " : " + round(pmi.ClaimPrice));
				} else {
					p.sendMessage(" " + caption("HelpClaimOther"));
				}
			} else if (allowedcmd.equalsIgnoreCase("auto")) {
				if (allowWorldTeleport) {
					p.sendMessage(" /plotme auto [World]");
				} else {
					p.sendMessage(" /plotme auto");
				}

				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					p.sendMessage(" " + caption("HelpAuto") + " " + caption("WordPrice") + " : " + round(pmi.ClaimPrice));
				} else {
					p.sendMessage(" " + caption("HelpAuto"));
				}
			} else if (allowedcmd.equalsIgnoreCase("home")) {
				if (allowWorldTeleport) {
					p.sendMessage(" /plotme home[:#] [World]");
				} else {
					p.sendMessage(" /plotme home[:#]");
				}

				if (ecoon && pmi != null && pmi.PlotHomePrice != 0) {
					p.sendMessage(" " + caption("HelpHome") + " " + caption("WordPrice") + " : " + round(pmi.PlotHomePrice));
				} else {
					p.sendMessage(" " + caption("HelpHome"));
				}
			} else if (allowedcmd.equalsIgnoreCase("home.other")) {
				if (allowWorldTeleport) {
					p.sendMessage(" /plotme home[:#] <" + caption("WordPlayer") + "> [World]");
				} else {
					p.sendMessage(" /plotme home[:#] <" + caption("WordPlayer") + ">");
				}

				if (ecoon && pmi != null && pmi.PlotHomePrice != 0) {
					p.sendMessage(" " + caption("HelpHomeOther") + " " + caption("WordPrice") + " : " + round(pmi.PlotHomePrice));
				} else {
					p.sendMessage(" " + caption("HelpHomeOther"));
				}
			} else if (allowedcmd.equalsIgnoreCase("info")) {
				p.sendMessage(" /plotme info");
				p.sendMessage(" " + caption("HelpInfo"));
			} else if (allowedcmd.equalsIgnoreCase("comment")) {
				p.sendMessage(" /plotme " + caption("CommandComment") + " <" + caption("WordComment") + ">");
				if (ecoon && pmi != null && pmi.AddCommentPrice != 0) {
					p.sendMessage(" " + caption("HelpComment") + " " + caption("WordPrice") + " : " + round(pmi.AddCommentPrice));
				} else {
					p.sendMessage(" " + caption("HelpComment"));
				}
			} else if (allowedcmd.equalsIgnoreCase("comments")) {
				p.sendMessage(" /plotme " + caption("CommandComments"));
				p.sendMessage(" " + caption("HelpComments"));
			} else if (allowedcmd.equalsIgnoreCase("list")) {
				p.sendMessage(" /plotme " + caption("CommandList"));
				p.sendMessage(" " + caption("HelpList"));
			} else if (allowedcmd.equalsIgnoreCase("listother")) {
				p.sendMessage(" /plotme " + caption("CommandList") + " <" + caption("WordPlayer") + ">");
				p.sendMessage(" " + caption("HelpListOther"));
			} else if (allowedcmd.equalsIgnoreCase("biomeinfo")) {
				p.sendMessage(" /plotme " + caption("CommandBiome"));
				p.sendMessage(" " + caption("HelpBiomeInfo"));
			} else if (allowedcmd.equalsIgnoreCase("biome")) {
				p.sendMessage(" /plotme " + caption("CommandBiome") + " <" + caption("WordBiome") + ">");
				if (ecoon && pmi != null && pmi.BiomeChangePrice != 0) {
					p.sendMessage(" " + caption("HelpBiome") + " " + caption("WordPrice") + " : " + round(pmi.BiomeChangePrice));
				} else {
					p.sendMessage(" " + caption("HelpBiome"));
				}
			} else if (allowedcmd.equalsIgnoreCase("biomelist")) {
				p.sendMessage(" /plotme " + caption("CommandBiomelist"));
				p.sendMessage(" " + caption("HelpBiomeList"));
			} else if (allowedcmd.equalsIgnoreCase("done")) {
				p.sendMessage(" /plotme " + caption("CommandDone"));
				p.sendMessage(" " + caption("HelpDone"));
			} else if (allowedcmd.equalsIgnoreCase("tp")) {
				if (allowWorldTeleport) {
					p.sendMessage(" /plotme " + caption("CommandTp") + " <id> [World]");
				} else {
					p.sendMessage(" /plotme " + caption("CommandTp") + " <id>");
				}

				p.sendMessage(" " + caption("HelpTp"));
			} else if (allowedcmd.equalsIgnoreCase("id")) {
				p.sendMessage(" /plotme id");
				p.sendMessage(" " + caption("HelpId"));
			} else if (allowedcmd.equalsIgnoreCase("clear")) {
				p.sendMessage(" /plotme " + caption("CommandClear"));
				if (ecoon && pmi != null && pmi.ClearPrice != 0) {
					p.sendMessage(" " + caption("HelpId") + " " + caption("WordPrice") + " : " + round(pmi.ClearPrice));
				} else {
					p.sendMessage(" " + caption("HelpClear"));
				}
			} else if (allowedcmd.equalsIgnoreCase("reset")) {
				p.sendMessage(" /plotme " + caption("CommandReset"));
				p.sendMessage(" " + caption("HelpReset"));
			} else if (allowedcmd.equalsIgnoreCase("add")) {
				p.sendMessage(" /plotme " + caption("CommandAdd") + " <" + caption("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.AddPlayerPrice != 0) {
					p.sendMessage(" " + caption("HelpAdd") + " " + caption("WordPrice") + " : " + round(pmi.AddPlayerPrice));
				} else {
					p.sendMessage(" " + caption("HelpAdd"));
				}
			} else if (allowedcmd.equalsIgnoreCase("deny")) {
				p.sendMessage(" /plotme " + caption("CommandDeny") + " <" + caption("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.DenyPlayerPrice != 0) {
					p.sendMessage(" " + caption("HelpDeny") + " " + caption("WordPrice") + " : " + round(pmi.DenyPlayerPrice));
				} else {
					p.sendMessage(" " + caption("HelpDeny"));
				}
			} else if (allowedcmd.equalsIgnoreCase("remove")) {
				p.sendMessage(" /plotme " + caption("CommandRemove") + " <" + caption("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.RemovePlayerPrice != 0) {
					p.sendMessage(" " + caption("HelpRemove") + " " + caption("WordPrice") + " : " + round(pmi.RemovePlayerPrice));
				} else {
					p.sendMessage(" " + caption("HelpRemove"));
				}
			} else if (allowedcmd.equalsIgnoreCase("undeny")) {
				p.sendMessage(" /plotme " + caption("CommandUndeny") + " <" + caption("WordPlayer") + ">");
				if (ecoon && pmi != null && pmi.UndenyPlayerPrice != 0) {
					p.sendMessage(" " + caption("HelpUndeny") + " " + caption("WordPrice") + " : " + round(pmi.UndenyPlayerPrice));
				} else {
					p.sendMessage(" " + caption("HelpUndeny"));
				}
			} else if (allowedcmd.equalsIgnoreCase("setowner")) {
				p.sendMessage(" /plotme " + caption("CommandSetowner") + " <" + caption("WordPlayer") + ">");
				p.sendMessage(" " + caption("HelpSetowner"));
			} else if (allowedcmd.equalsIgnoreCase("move")) {
				p.sendMessage(" /plotme " + caption("CommandMove") + " <" + caption("WordIdFrom") + "> <" + caption("WordIdTo") + ">");
				p.sendMessage(" " + caption("HelpMove"));
			} else if (allowedcmd.equalsIgnoreCase("expired")) {
				p.sendMessage(" /plotme " + caption("CommandExpired") + " [page]");
				p.sendMessage(" " + caption("HelpExpired"));
			} else if (allowedcmd.equalsIgnoreCase("donelist")) {
				p.sendMessage(" /plotme " + caption("CommandDoneList") + " [page]");
				p.sendMessage(" " + caption("HelpDoneList"));
			} else if (allowedcmd.equalsIgnoreCase("addtime")) {
				p.sendMessage(" /plotme " + caption("CommandAddtime"));
				int days = (pmi == null) ? 0 : pmi.DaysToExpiration;
				if (days == 0) {
					p.sendMessage(" " + caption("HelpAddTime1") + " Never");
				} else {
					p.sendMessage(" " + caption("HelpAddTime1") + " " + days + " " + caption("HelpAddTime2"));
				}
			} else if (allowedcmd.equalsIgnoreCase("reload")) {
				p.sendMessage(" /plotme reload");
				p.sendMessage(" " + caption("HelpReload"));
			} else if (allowedcmd.equalsIgnoreCase("dispose")) {
				p.sendMessage(" /plotme " + caption("CommandDispose"));
				if (ecoon && pmi != null && pmi.DisposePrice != 0) {
					p.sendMessage(" " + caption("HelpDispose") + " " + caption("WordPrice") + " : " + round(pmi.DisposePrice));
				} else {
					p.sendMessage(" " + caption("HelpDispose"));
				}
			} else if (allowedcmd.equalsIgnoreCase("buy")) {
				p.sendMessage(" /plotme " + caption("CommandBuy"));
				p.sendMessage(" " + caption("HelpBuy"));
			} else if (allowedcmd.equalsIgnoreCase("sell")) {
				p.sendMessage(" /plotme " + caption("CommandSell") + " [amount]");
				p.sendMessage(" " + caption("HelpSell") + " " + caption("WordDefault") + " : " + round(pmi.SellToPlayerPrice));
			} else if (allowedcmd.equalsIgnoreCase("sellbank")) {
				p.sendMessage(" /plotme " + caption("CommandSellBank"));
				p.sendMessage(" " + caption("HelpSellBank") + " " + round(pmi.SellToBankPrice));
			} else if (allowedcmd.equalsIgnoreCase("auction")) {
				p.sendMessage(" /plotme " + caption("CommandAuction") + " [amount]");
				p.sendMessage(" " + caption("HelpAuction") + " " + caption("WordDefault") + " : 1");
			} else if (allowedcmd.equalsIgnoreCase("resetexpired")) {
				p.sendMessage(" /plotme resetexpired <World>");
				p.sendMessage(" " + caption("HelpResetExpired"));
			} else if (allowedcmd.equalsIgnoreCase("bid")) {
				p.sendMessage(" /plotme bid <amount>");
				p.sendMessage(" " + caption("HelpBid"));
			}
		}

		return true;
	}

	private boolean tp(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.tp")) {
			if (!PlotManager.isPlotWorld(p) && !allowWorldTeleport) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				if (args.length == 2 || (args.length == 3 && allowWorldTeleport)) {
					String id = args[1];

					if (!PlotManager.isValidId(id)) {
						if (allowWorldTeleport) {
							Send(p, "Usage: /plotme " + caption("CommandTp") + " <id> [World] Example: /plotme " + caption("CommandTp") + " 5;-1 ");
						} else {
							Send(p, "Usage: /plotme " + caption("CommandTp") + " <id> Example: /plotme " + caption("CommandTp") + " 5;-1 ");
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
							Send(p, caption("MsgNoPlotworldFound"));
						} else {
							Location bottom = PlotManager.getPlotBottomLoc(w, id);
							Location top = PlotManager.getPlotTopLoc(w, id);

							p.teleport(new Location(w, bottom.getX() + (top.getBlockX() - bottom.getBlockX()) / 2, PlotManager.getMap(w).RoadHeight + 2, bottom.getZ() - 2));
						}
					}
				} else {
					if (allowWorldTeleport) {
						Send(p, "Usage: /plotme " + caption("CommandTp") + " <id> [World] Example: /plotme " + caption("CommandTp") + " 5;-1 ");
					} else {
						Send(p, "Usage: /plotme " + caption("CommandTp") + " <id> Example: /plotme " + caption("CommandTp") + " 5;-1 ");
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean auto(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.auto")) {
			if (!PlotManager.isPlotWorld(p) && !allowWorldTeleport) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				World w;

				if (!PlotManager.isPlotWorld(p) && allowWorldTeleport) {
					if (args.length == 2) {
						w = Bukkit.getWorld(args[1]);
					} else {
						w = PlotManager.getFirstWorld();
					}

					if (w == null || !PlotManager.isPlotWorld(w)) {
						Send(p, args[1] + " " + caption("MsgWorldNotPlot"));
						return true;
					}
				} else {
					w = p.getWorld();
				}

				if (w == null) {
					Send(p, caption("MsgNoPlotworldFound"));
				} else {
					if (PlotManager.getNbOwnedPlot(p, w) >= getPlotLimit(p) && !p.hasPermission("PlotMe.admin")) {
						Send(p, caption("MsgAlreadyReachedMaxPlots") + " (" +
								PlotManager.getNbOwnedPlot(p, w) + "/" + getPlotLimit(p) + "). Use /plotme home " + caption("MsgToGetToIt"));
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

										p.teleport(new Location(w, PlotManager.bottomX(plot.id, w) + (PlotManager.topX(plot.id, w) -
												PlotManager.bottomX(plot.id, w)) / 2, pmi.RoadHeight + 2, PlotManager.bottomZ(plot.id, w) - 2));

										Send(p, caption("MsgThisPlotYours") + " Use /plotme home " + caption("MsgToGetToIt") + " " + f(-price));

										logger.info("[PlotMe Event] " + name + " " + caption("MsgClaimedPlot") + " " + id + ((price != 0) ? " for " + price : ""));

										return true;
									}
								}
							}
						}

						Send(p, caption("MsgNoPlotFound1") + " " + (limit ^ 2) + " " + caption("MsgNoPlotFound2"));
					}
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
						Send(p, caption("MsgAlreadyReachedMaxPlots") + " (" +
								PlotManager.getNbOwnedPlot(p) + "/" + getPlotLimit(p) + "). Use /plotme home " + caption("MsgToGetToIt"));
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
								Send(p, caption("MsgThisPlotYours") + " Use /plotme home " + caption("MsgToGetToIt") + " " + f(-price));
							} else {
								Send(p, caption("MsgThisPlotIsNow") + " " + playername + caption("WordPossessive") + ". Use /plotme home " + caption("MsgToGetToIt") + " " + f(-price));
							}

							logger.info("[PlotMe Event] " + playername + " " + caption("MsgClaimedPlot") + " " + id + ((price != 0) ? " for " + price : ""));
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
							Send(p, "Usage: /plotme home:# " +
									"Example: /plotme home:1");
							return true;
						} else {
							nb = Integer.parseInt(args[0].split(":")[1]);
						}
					} catch (Exception ex) {
						Send(p, "Usage: /plotme home:# " +
								"Example: /plotme home:1");
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
						Send(p, args[2] + " " + caption("MsgWorldNotPlot"));
						return true;
					} else {
						w = Bukkit.getWorld(args[2]);
						worldname = args[2];
					}
				}

				if (!PlotManager.isPlotWorld(w)) {
					Send(p, worldname + " " + caption("MsgWorldNotPlot"));
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
							} else {
								i--;
							}
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
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						p.sendMessage(caption("InfoId") + ": " + id + " " + caption("InfoOwner") + ": " + plot.owner +
								" " + caption("InfoBiome") + ": " + FormatBiome(plot.biome.name()));

						p.sendMessage(caption("InfoExpire") + ": " + ((plot.expireddate == null) ? "Never" : plot.expireddate.toString()) +
								" " + caption("InfoFinished") + ": " + ((plot.finished) ? "Yes" : "No") + " " + caption("InfoProtected") + ": " + ((plot.protect) ? "Yes" : "No"));

						if (plot.allowedcount() > 0) {
							p.sendMessage(caption("InfoHelpers") + ": " + plot.getAllowed());
						}

						if (allowToDeny && plot.deniedcount() > 0) {
							p.sendMessage(caption("InfoDenied") + ": " + plot.getDenied());
						}

						if (PlotManager.isEconomyEnabled(p)) {
							if (plot.currentbidder == null || plot.currentbidder.equalsIgnoreCase("")) {
								p.sendMessage(caption("InfoAuctionned") + ": " + ((plot.auctionned) ? "Yes" +
										" " + caption("InfoMinimumBid") + ": " + round(plot.currentbid) : "No") +
										" " + caption("InfoForSale") + ": " + ((plot.forsale) ? round(plot.customprice) : "No"));
							} else {
								p.sendMessage(caption("InfoAuctionned") + ": " + ((plot.auctionned) ? "Yes" +
										" " + caption("InfoBidder") + ": " + plot.currentbidder +
										" Bid: " + round(plot.currentbid) : "No") + " " + caption("InfoForSale") + ": " + ((plot.forsale) ? round(plot.customprice) : "No"));
							}
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
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
			} else {
				if (args.length < 2) {
					Send(p, "Usage: /plotme " + caption("CommandComment") + " <text>");
				} else {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						Send(p, caption("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
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

							logger.info("[PlotMe Event] " + playername + " " + caption("MsgCommentedPlot") + " " + id + ((price != 0) ? " for " + price : ""));
						} else {
							Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
						}
					}
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
			} else {
				if (args.length < 2) {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						Send(p, caption("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							if (plot.ownerId.equals(p.getUniqueId()) || plot.isAllowed(p.getUniqueId()) || p.hasPermission("PlotMe.admin")) {
								if (plot.comments.isEmpty()) {
									Send(p, caption("MsgNoComments"));
								} else {
									Send(p, caption("MsgYouHave") + " " +
											plot.comments.size() + " " + caption("MsgComments"));

									for (String[] comment : plot.comments) {
										p.sendMessage("From : " + comment[0]);
										p.sendMessage("" + comment[1]);
									}

								}
							} else {
								Send(p, "This plot(" + id + ") " + caption("MsgNotYoursNotAllowedViewComments"));
							}
						} else {
							Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
						}
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
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
					p.sendMessage(caption("MsgNoPlotFound"));
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

									logger.info("[PlotMe Event] " + playername + " " + caption("MsgChangedBiome") + " " + id + " to " +
											FormatBiome(biome.name()) + ((price != 0) ? " for " + price : ""));
								} else {
									Send(p, "This plot(" + id + ") " + caption("MsgNotYoursNotAllowedBiome"));
								}
							}
						} else {
							Plot plot = plotmaps.get(w.getName().toLowerCase()).plots.get(id);

							Send(p, caption("MsgPlotUsingBiome") + " " + FormatBiome(plot.biome.name()));
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean biomelist(CommandSender s, String[] args) {
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
			Send(s, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean reset(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.reset")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				Plot plot = PlotManager.getPlotById(p.getLocation());

				if (plot == null) {
					Send(p, caption("MsgNoPlotFound"));
				} else {
					if (plot.protect) {
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
												Send(player, "Plot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgWasReset") + " " + f(plot.currentbid));
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
								} else {
									for (Player player : Bukkit.getServer().getOnlinePlayers()) {
										if (player.getName().equalsIgnoreCase(plot.owner)) {
											Send(player, "Plot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgWasReset") + " " + f(pmi.ClaimPrice));
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

						Send(p, caption("MsgPlotReset"));

						logger.info("[PlotMe Event] " + name + " " + caption("MsgResetPlot") + " " + id);
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean clear(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.clear") || p.hasPermission("PlotMe.use.clear")) {
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

								logger.info("[PlotMe Event] " + playername + " " + caption("MsgClearedPlot") + " " + id + ((price != 0) ? " for " + price : ""));
							} else {
								Send(p, "This plot(" + id + ") " + caption("MsgNotYoursNotAllowedClear"));
							}
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
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
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
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

									logger.info("[PlotMe Event] " + playername + " " + caption("MsgAddedPlayer") + " " + allowed + " " + caption("MsgToPlot") + " " + id + ((price != 0) ? " for " + price : ""));
								}
							} else {
								Send(p, "This plot(" + id + ") " + caption("MsgNotYoursNotAllowedAdd"));
							}
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
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
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
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

									logger.info("[PlotMe Event] " + playername + " " + caption("MsgDeniedPlayer") + " " + denied + " " + caption("MsgToPlot") + " " + id + ((price != 0) ? " for " + price : ""));
								}
							} else {
								Send(p, "This plot(" + id + ") " + caption("MsgNotYoursNotAllowedDeny"));
							}
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
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
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
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

									logger.info("[PlotMe Event] " + p.getName() + " " + caption("MsgRemovedPlayer") + " " + allowed + " " + caption("MsgFromPlot") + " " + id + ((price != 0) ? " for " + price : ""));
								} else {
									Send(p, caption("WordPlayer") + " " + args[1] + " " + caption("MsgWasNotAllowed"));
								}
							} else {
								Send(p, "This plot(" + id + ") " + caption("MsgNotYoursNotAllowedRemove"));
							}
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
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

									logger.info("[PlotMe Event] " + playername + " " + caption("MsgUndeniedPlayer") + " " + denied + " " + caption("MsgFromPlot") + " " + id + ((price != 0) ? " for " + price : ""));
								} else {
									Send(p, caption("WordPlayer") + " " + args[1] + " " + caption("MsgWasNotDenied"));
								}
							} else {
								Send(p, "This plot(" + id + ") " + caption("MsgNotYoursNotAllowedUndeny"));
							}
						}
					} else {
						Send(p, "This plot(" + id + ") " + caption("MsgHasNoOwner"));
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
				} else {
					if (args.length < 2 || args[1].isEmpty()) {
						Send(p, "Usage: /plotme " + caption("CommandSetowner") + " <" + caption("WordPlayer") + ">");
					} else {
						String newowner = args[1];
						String oldowner = "<" + caption("WordNotApplicable") + ">";
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
									} else {
										for (Player player : Bukkit.getServer().getOnlinePlayers()) {
											if (player.getName().equalsIgnoreCase(oldowner)) {
												Send(player, caption("MsgYourPlot") + " " + id + " " + caption("MsgNowOwnedBy") + " " + newowner + ". " + f(pmi.ClaimPrice));
												break;
											}
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
												Send(player, "Plot " + id + " " + caption("MsgChangedOwnerFrom") + " " + oldowner + " to " + newowner + ". " + f(plot.currentbid));
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

						logger.info("[PlotMe Event] " + playername + " " + caption("MsgChangedOwnerOf") + " " + id + " From " + oldowner + " to " + newowner);
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean id(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.id")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				String plotid = PlotManager.getPlotId(p.getLocation());

				if (plotid.isEmpty()) {
					Send(p, caption("MsgNoPlotFound"));
				} else {
					p.sendMessage("Plot id: " + plotid);

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

	private boolean move(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.move")) {
			if (!PlotManager.isPlotWorld(p)) {
				Send(p, caption("MsgNotPlotWorld"));
			} else {
				if (args.length < 3 || args[1].equalsIgnoreCase("") || args[2].equalsIgnoreCase("")) {
					Send(p, "Usage: /plotme " + caption("CommandMove") + " <" + caption("WordIdFrom") + "> <" + caption("WordIdTo") + "> " +
							"Example: /plotme " + caption("CommandMove") + " 0;1 2;-1");
				} else {
					String plot1 = args[1];
					String plot2 = args[2];

					if (!PlotManager.isValidId(plot1) || !PlotManager.isValidId(plot2)) {
						Send(p, "Usage: /plotme " + caption("CommandMove") + " <" + caption("WordIdFrom") + "> <" + caption("WordIdTo") + "> " +
								"Example: /plotme " + caption("CommandMove") + " 0;1 2;-1");
						return true;
					} else {
						if (PlotManager.movePlot(p.getWorld(), plot1, plot2)) {
							Send(p, caption("MsgPlotMovedSuccess"));

							logger.info("[PlotMe Event] " + p.getName() + " " + caption("MsgExchangedPlot") + " " + plot1 + " " + caption("MsgAndPlot") + " " + plot2);
						} else {
							Send(p, caption("ErrMovingPlot"));
						}
					}
				}
			}
		} else {
			Send(p, caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean reload(CommandSender s, String[] args) {
		if (!(s instanceof Player) || s.hasPermission("PlotMe.admin.reload")) {
			plugin.initialize();
			Send(s, caption("MsgReloadedSuccess"));

			logger.info("[PlotMe Event] " + s.getName() + " " + caption("MsgReloadedConfigurations"));
		} else {
			Send(s, caption("MsgPermissionDenied"));
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
		logger.warning("[PlotMe Event] " + msg);
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
			format = (price <= 1 && price >= -1) ? format + " " + economy.currencyNameSingular() : format + " " + economy.currencyNamePlural();
		}

		if (showsign) {
			return ((price > 0) ? "+" + format : "-" + format);
		} else {
			return format;
		}
	}

	private void Send(CommandSender cs, String text) {
		cs.sendMessage(text);
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
