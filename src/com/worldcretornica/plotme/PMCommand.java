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
		if (label.equalsIgnoreCase("plotme")) {
			if (!(sender instanceof Player)) {
				//<editor-fold desc="Console Reload Help Message">
				if (args.length == 0) {
					sender.sendMessage("To reload PlotMe use the command : \"plotme reload\"");
					return true;
				}
				// </editor-fold>
				// <editor-fold desc="Reload from console">
				else {
					if (args[0].equalsIgnoreCase("reload")) {
						return reload(sender);
					}
				}
				//</editor-fold>
			} else {
				Player player = (Player) sender;

				if (args.length == 0) {
					return showhelp(player, 1);
				} else {
					String a0 = args[0];
					int ipage = -1;

					try {
						ipage = Integer.parseInt(a0);
					} catch (NumberFormatException ignored) {
					}

					if (ipage != -1) {
						return showhelp(player, ipage);
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
								return showhelp(player, ipage);
							} else {
								return showhelp(player, 1);
							}
						}
						if (a0.equalsIgnoreCase(caption("CommandClaim"))) {
							return claim(player, args);
						}
						if (a0.equalsIgnoreCase("auto")) {
							return auto(player);
						}
						if (a0.equalsIgnoreCase("info") || a0.equalsIgnoreCase("i")) {
							return info(player);
						}
						if (a0.equalsIgnoreCase(caption("CommandComment"))) {
							return comment(player, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandComments")) || a0.equalsIgnoreCase("command")) {
							return comments(player, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandBiome")) || a0.equalsIgnoreCase("b")) {
							return biome(player, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandBiomelist"))) {
							return biomelist(player);
						}
						if (a0.equalsIgnoreCase("id")) {
							return id(player);
						}
						if (a0.equalsIgnoreCase("tp")) {
							return tp(player, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandClear"))) {
							return clear(player);
						}
						if (a0.equalsIgnoreCase(caption("CommandReset"))) {
							return reset(player);
						}
						if (a0.equalsIgnoreCase(caption("CommandAdd")) || a0.equalsIgnoreCase("+")) {
							return add(player, args);
						}
						if (allowToDeny) {
							if (a0.equalsIgnoreCase("deny")) {
								return deny(player, args);
							}
							if (a0.equalsIgnoreCase("undeny")) {
								return undeny(player, args);
							}
						}
						if (a0.equalsIgnoreCase(caption("CommandRemove")) || a0.equalsIgnoreCase("-")) {
							return remove(player, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandSetowner")) || a0.equalsIgnoreCase("o")) {
							return setowner(player, args);
						}
						if (a0.equalsIgnoreCase("move") || a0.equalsIgnoreCase("m")) {
							return move(player, args);
						}
						if (a0.equalsIgnoreCase("reload")) {
							return reload(sender);
						}
						if (a0.equalsIgnoreCase(caption("CommandList"))) {
							return plotlist(player, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandExpired"))) {
							return expired(player, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandAddtime"))) {
							return addtime(player);
						}
						if (a0.equalsIgnoreCase(caption("CommandDone"))) {
							return done(player);
						}
						if (a0.equalsIgnoreCase(caption("CommandDoneList"))) {
							return donelist(player, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandProtect"))) {
							return protect(player);
						}
						if (a0.equalsIgnoreCase(caption("CommandSell"))) {
							return sell(player, args);
						}
						if (a0.equalsIgnoreCase(caption("CommandDispose"))) {
							return dispose(player);
						}
						if (a0.equalsIgnoreCase("auction")) {
							return auction(player, args);
						}
						if (a0.equalsIgnoreCase("buy")) {
							return buy(player);
						}
						if (a0.equalsIgnoreCase("bid")) {
							return bid(player, args);
						}
						if (a0.startsWith("home")) {
							return home(player, args);
						}
						if (a0.equalsIgnoreCase("resetexpired")) {
							return resetexpired(player);
						}
					}
				}
			}
			//</editor-fold>
		}
		return false;
	}

	private boolean resetexpired(Player player) {
		if (player.hasPermission("PlotMe.admin.resetexpired")) {
			if (worldcurrentlyprocessingexpired != null) {
				player.sendMessage(cscurrentlyprocessingexpired.getName() + " " + caption("MsgAlreadyProcessingPlots"));
			} else {
				if (Bukkit.getWorld("plotworld") == null) {
					player.sendMessage("World \"plotworld\" " + caption("MsgDoesNotExistOrNotLoaded"));
					return false;
				}
				worldcurrentlyprocessingexpired = Bukkit.getWorld("plotworld");
				cscurrentlyprocessingexpired = player;
				counterexpired = 50;
				nbperdeletionprocessingexpired = 5;

				plugin.scheduleTask(new PlotRunnableDeleteExpire());
			}
		} else {
			player.sendMessage(caption("MsgPermissionDenied"));
			return false;
		}
		return true;
	}

	private boolean bid(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			if (p.hasPermission("PlotMe.use.bid")) {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					Plot plot = PlotManager.getPlotById(p, id);

					if (plot.auctionned) {
						String bidder = p.getName();

						if (plot.owner.equalsIgnoreCase(bidder)) {
							p.sendMessage(caption("MsgCannotBidOwnPlot"));
						} else if (args.length == 2) {
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

										logger.info("[Event] " + bidder + " bid " + bid + " on plot " + id);
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
					} else {
						p.sendMessage(caption("MsgPlotNotAuctionned"));
					}
				} else {
					p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
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
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (!plot.forsale) {
							p.sendMessage(caption("MsgPlotNotForSale"));
						} else {
							String buyer = p.getName();

							if (plot.owner.equalsIgnoreCase(buyer)) {
								p.sendMessage(caption("MsgCannotBuyOwnPlot"));
							} else {
								int plotlimit = getPlotLimit(p);

								if (plotlimit != -1 && PlotManager.getNbOwnedPlot(p) >= plotlimit) {
									p.sendMessage(caption("MsgAlreadyReachedMaxPlots") + " (" +
											PlotManager.getNbOwnedPlot(p) + "/" + getPlotLimit(p) + "). " +
											"Use /plotme home " + caption("MsgToGetToIt"));
								} else {
									World w = p.getWorld();

									double cost = plot.customprice;

									if (economy.getBalance(buyer) < cost) {
										p.sendMessage(caption("MsgNotEnoughBuy"));
									} else {
										EconomyResponse er = economy.withdrawPlayer(buyer, cost);

										if (er.transactionSuccess()) {
											String oldowner = plot.owner;

											if (!oldowner.equalsIgnoreCase("$Bank$")) {
												EconomyResponse er2 = economy.depositPlayer(oldowner, cost);

												if (!er2.transactionSuccess()) {
													p.sendMessage(er2.errorMessage);
													warn(er2.errorMessage);
												} else {
													for (Player player : Bukkit.getServer().getOnlinePlayers()) {
														if (player.getName().equalsIgnoreCase(oldowner)) {
															player.sendMessage("Plot " + id + " " +
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

											p.sendMessage(caption("MsgPlotBought") + " " + f(-cost));

											logger.info("[Event] " + buyer + " " + caption("MsgBoughtPlot") + " " + id + " for " + cost);
										} else {
											p.sendMessage(er.errorMessage);
											warn(er.errorMessage);
										}
									}
								}
							}
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

	private boolean auction(Player p, String[] args) {
		if (PlotManager.isEconomyEnabled(p)) {
			PlotMapInfo pmi = PlotManager.getMap(p);

			if (pmi.CanPutOnSale) {
				if (p.hasPermission("PlotMe.use.auction") || p.hasPermission("PlotMe.admin.auction")) {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						p.sendMessage(caption("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							String name = p.getName();

							if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.auction")) {
								World w = p.getWorld();

								if (plot.auctionned) {
									if (plot.currentbidder != null && !plot.currentbidder.equalsIgnoreCase("") && !p.hasPermission("PlotMe.admin.auction")) {
										p.sendMessage(caption("MsgPlotHasBidsAskAdmin"));
									} else {
										if (plot.currentbidder != null && !plot.currentbidder.equalsIgnoreCase("")) {
											EconomyResponse er = economy.depositPlayer(plot.currentbidder, plot.currentbid);

											if (!er.transactionSuccess()) {
												p.sendMessage(er.errorMessage);
												warn(er.errorMessage);
											} else {
												for (Player player : Bukkit.getServer().getOnlinePlayers()) {
													if (plot.currentbidder != null && player.getName().equalsIgnoreCase(plot.currentbidder)) {
														player.sendMessage(caption("MsgAuctionCancelledOnPlot") + " " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + ". " + f(plot.currentbid));
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

										p.sendMessage(caption("MsgAuctionCancelled"));

										logger.info("[Event] " + name + " " + caption("MsgStoppedTheAuctionOnPlot") + " " + id);
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
										p.sendMessage(caption("MsgInvalidAmount"));
									} else {
										plot.currentbid = bid;
										plot.auctionned = true;
										PlotManager.adjustWall(p.getLocation());
										PlotManager.setSellSign(w, plot);

										plot.updateField("currentbid", bid);
										plot.updateField("auctionned", true);

										p.sendMessage(caption("MsgAuctionStarted"));

										logger.info("[Event] " + name + " " + caption("MsgStartedAuctionOnPlot") + " " + id + " at " + bid);
									}
								}
							} else {
								p.sendMessage(caption("MsgDoNotOwnPlot"));
							}
						} else {
							p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
						}
					}
				} else {
					p.sendMessage(caption("MsgPermissionDenied"));
				}
			} else {
				p.sendMessage(caption("MsgSellingPlotsIsDisabledWorld"));
			}
		} else {
			p.sendMessage(caption("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean dispose(Player p) {
		if (p.hasPermission("PlotMe.admin.dispose") || p.hasPermission("PlotMe.use.dispose")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot.protect) {
							p.sendMessage(caption("MsgPlotProtectedNotDisposed"));
						} else {
							String name = p.getName();

							if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.dispose")) {
								PlotMapInfo pmi = PlotManager.getMap(p);

								double cost = pmi.DisposePrice;

								if (PlotManager.isEconomyEnabled(p)) {
									if (cost != 0 && economy.getBalance(name) < cost) {
										p.sendMessage(caption("MsgNotEnoughDispose"));
										return true;
									}

									EconomyResponse er = economy.withdrawPlayer(name, cost);

									if (!er.transactionSuccess()) {
										p.sendMessage(er.errorMessage);
										warn(er.errorMessage);
										return true;
									}

									if (plot.auctionned) {
										String currentbidder = plot.currentbidder;

										if (!currentbidder.isEmpty()) {
											EconomyResponse er2 = economy.depositPlayer(currentbidder, plot.currentbid);

											if (!er2.transactionSuccess()) {
												p.sendMessage(er2.errorMessage);
												warn(er2.errorMessage);
											} else {
												for (Player player : Bukkit.getServer().getOnlinePlayers()) {
													if (player.getName().equalsIgnoreCase(currentbidder)) {
														player.sendMessage("Plot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgWasDisposed") + " " + f(cost));
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

								p.sendMessage(caption("MsgPlotDisposedAnyoneClaim"));

								logger.info("[Event] " + name + " " + caption("MsgDisposedPlot") + " " + id);
							} else {
								p.sendMessage("This plot(" + id + ") " + caption("MsgNotYoursCannotDispose"));
							}
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
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
						p.sendMessage(caption("MsgNoPlotFound"));
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

									p.sendMessage(caption("MsgPlotNoLongerSale"));

									logger.info("[Event] " + name + " " + caption("MsgRemovedPlot") + " " + id + " " + caption("MsgFromBeingSold"));
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
													p.sendMessage(er.errorMessage);
													warn(er.errorMessage);
												} else {
													for (Player player : Bukkit.getServer().getOnlinePlayers()) {
														if (player.getName().equalsIgnoreCase(currentbidder)) {
															player.sendMessage("Plot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgSoldToBank") + " " + f(bid));
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

												p.sendMessage(caption("MsgPlotSold") + " " + f(sellprice));

												logger.info("[Event] " + name + " " + caption("MsgSoldToBankPlot") + " " + id + " for " + sellprice);
											} else {
												p.sendMessage(" " + er.errorMessage);
												warn(er.errorMessage);
											}
										}
									} else {
										if (price < 0) {
											p.sendMessage(caption("MsgInvalidAmount"));
										} else {
											plot.customprice = price;
											plot.forsale = true;

											plot.updateField("customprice", price);
											plot.updateField("forsale", true);

											PlotManager.adjustWall(l);
											PlotManager.setSellSign(w, plot);

											p.sendMessage(caption("MsgPlotForSale"));

											logger.info("[Event] " + name + " " + caption("MsgPutOnSalePlot") + " " + id + " for " + price);
										}
									}
								}
							} else {
								p.sendMessage(caption("MsgDoNotOwnPlot"));
							}
						} else {
							p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
						}
					}
				} else {
					p.sendMessage(caption("MsgPermissionDenied"));
				}
			} else {
				p.sendMessage(caption("MsgSellingPlotsIsDisabledWorld"));
			}
		} else {
			p.sendMessage(caption("MsgEconomyDisabledWorld"));
		}
		return true;
	}

	private boolean protect(Player p) {
		if (p.hasPermission("PlotMe.admin.protect") || p.hasPermission("PlotMe.use.protect")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						String name = p.getName();

						if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.protect")) {
							if (plot.protect) {
								plot.protect = false;
								PlotManager.adjustWall(p.getLocation());

								plot.updateField("protected", false);

								p.sendMessage(caption("MsgPlotNoLongerProtected"));

								logger.info("[Event] " + name + " " + caption("MsgUnprotectedPlot") + " " + id);
							} else {
								PlotMapInfo pmi = PlotManager.getMap(p);

								double cost = 0;

								if (PlotManager.isEconomyEnabled(p)) {
									cost = pmi.ProtectPrice;

									if (economy.getBalance(name) < cost) {
										p.sendMessage(caption("MsgNotEnoughProtectPlot"));
										return true;
									} else {
										EconomyResponse er = economy.withdrawPlayer(name, cost);

										if (!er.transactionSuccess()) {
											p.sendMessage(er.errorMessage);
											warn(er.errorMessage);
											return true;
										}
									}

								}

								plot.protect = true;
								PlotManager.adjustWall(p.getLocation());

								plot.updateField("protected", true);

								p.sendMessage(caption("MsgPlotNowProtected") + " " + f(-cost));

								logger.info("[Event] " + name + " " + caption("MsgProtectedPlot") + " " + id);

							}
						} else {
							p.sendMessage(caption("MsgDoNotOwnPlot"));
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean donelist(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.done")) {
			PlotMapInfo pmi = PlotManager.getMap(p);

			if (pmi == null) {
				p.sendMessage(caption("MsgNotPlotWorld"));
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

				if (finishedplots.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotsFinished"));
				} else {
					p.sendMessage(caption("MsgFinishedPlotsPage") + " " + page + "/" + maxpage);

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
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean done(Player p) {
		if (p.hasPermission("PlotMe.use.done") || p.hasPermission("PlotMe.admin.done")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);
						String name = p.getName();

						if (plot.owner.equalsIgnoreCase(name) || p.hasPermission("PlotMe.admin.done")) {
							if (plot.finished) {
								plot.setUnfinished();
								p.sendMessage(caption("MsgUnmarkFinished"));

								logger.info("[Event] " + name + " marked " + id + " finished");
							} else {
								plot.setFinished();
								p.sendMessage(caption("MsgMarkFinished"));

								logger.info("[Event] " + name + " marked " + id + " unfinished");
							}
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean addtime(Player p) {
		if (p.hasPermission("PlotMe.admin.addtime")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
				return true;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot != null) {
							String name = p.getName();

							plot.resetExpire(PlotManager.getMap(p).DaysToExpiration);
							p.sendMessage(caption("MsgPlotExpirationReset"));

							logger.info("[Event] " + name + " reset expiration on plot " + id);
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean expired(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.expired")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
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
					p.sendMessage(caption("MsgNoPlotExpired"));
				} else {
					p.sendMessage(caption("MsgExpiredPlotsPage") + " " + page + "/" + maxpage);

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
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}


	private boolean plotlist(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.list")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
				return true;
			} else {
				String name;
				String pname = p.getName();

				if (p.hasPermission("PlotMe.admin.list") && args.length == 2) {
					name = args[1];
					p.sendMessage(caption("MsgListOfPlotsWhere") + " " + name + " " + caption("MsgCanBuild"));
				} else {
					name = p.getName();
					p.sendMessage(caption("MsgListOfPlotsWhereYou"));
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
								p.sendMessage("  " + plot.id + " -> Yours" + addition + ", Helpers: " + helpers);
							} else {
								p.sendMessage("  " + plot.id + " -> " + plot.owner + addition + ", Helpers: " + helpers);
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
							p.sendMessage("  " + plot.id + " -> Yours" + addition + ", Helpers: " + helpers);
						} else {
							p.sendMessage("  " + plot.id + " -> " + plot.owner + "'s" + addition + ", Helpers: " + helpers);
						}
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
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

		if (p.getWorld() == Bukkit.getWorld("plotworld") && ecoon) {
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
				World w;

				if (p.getWorld() == Bukkit.getWorld("plotworld")) {
					w = p.getWorld();
				} else {
					w = Bukkit.getWorld("plotworld");
				}

				int maxplots = getPlotLimit(p);
				int ownedplots = PlotManager.getNbOwnedPlot(p, w);

				if (maxplots == -1) {
					p.sendMessage(caption("HelpYourPlotLimitWorld") + " : " + ownedplots + " " + caption("HelpUsedOf") + " Infinite");
				} else {
					p.sendMessage(caption("HelpYourPlotLimitWorld") + " : " + ownedplots + " " + caption("HelpUsedOf") + " " + maxplots);
				}
			} else if (allowedcmd.equalsIgnoreCase("claim")) {
				p.sendMessage(" /plotme " + caption("CommandClaim"));
				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					p.sendMessage(" " + caption("HelpClaim") + " Price : " + round(pmi.ClaimPrice));
				} else {
					p.sendMessage(" " + caption("HelpClaim"));
				}
			} else if (allowedcmd.equalsIgnoreCase("claim.other")) {
				p.sendMessage(" /plotme " + caption("CommandClaim") + " <Player>");
				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					p.sendMessage(" " + caption("HelpClaimOther") + " Price : " + round(pmi.ClaimPrice));
				} else {
					p.sendMessage(" " + caption("HelpClaimOther"));
				}
			} else if (allowedcmd.equalsIgnoreCase("auto")) {
				p.sendMessage(" /plotme auto");

				if (ecoon && pmi != null && pmi.ClaimPrice != 0) {
					p.sendMessage(" " + caption("HelpAuto") + " Price : " + round(pmi.ClaimPrice));
				} else {
					p.sendMessage(" " + caption("HelpAuto"));
				}
			} else if (allowedcmd.equalsIgnoreCase("home")) {
				p.sendMessage(" /plotme home[:#]");

				if (ecoon && pmi != null && pmi.PlotHomePrice != 0) {
					p.sendMessage(" " + caption("HelpHome") + " Price : " + round(pmi.PlotHomePrice));
				} else {
					p.sendMessage(" " + caption("HelpHome"));
				}
			} else if (allowedcmd.equalsIgnoreCase("home.other")) {
				p.sendMessage(" /plotme home[:#] <Player>");

				if (ecoon && pmi != null && pmi.PlotHomePrice != 0) {
					p.sendMessage(" " + caption("HelpHomeOther") + " Price : " + round(pmi.PlotHomePrice));
				} else {
					p.sendMessage(" " + caption("HelpHomeOther"));
				}
			} else if (allowedcmd.equalsIgnoreCase("info")) {
				p.sendMessage(" /plotme info");
				p.sendMessage(" " + caption("HelpInfo"));
			} else if (allowedcmd.equalsIgnoreCase("comment")) {
				p.sendMessage(" /plotme " + caption("CommandComment") + " <comment>");
				if (ecoon && pmi != null && pmi.AddCommentPrice != 0) {
					p.sendMessage(" " + caption("HelpComment") + " Price : " + round(pmi.AddCommentPrice));
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
				p.sendMessage(" /plotme " + caption("CommandList") + " <Player>");
				p.sendMessage(" " + caption("HelpListOther"));
			} else if (allowedcmd.equalsIgnoreCase("biomeinfo")) {
				p.sendMessage(" /plotme " + caption("CommandBiome"));
				p.sendMessage(" " + caption("HelpBiomeInfo"));
			} else if (allowedcmd.equalsIgnoreCase("biome")) {
				p.sendMessage(" /plotme " + caption("CommandBiome") + " <biome>");
				if (ecoon && pmi != null && pmi.BiomeChangePrice != 0) {
					p.sendMessage(" " + caption("HelpBiome") + " Price : " + round(pmi.BiomeChangePrice));
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
				p.sendMessage(" /plotme tp <id>");
				p.sendMessage(" " + caption("HelpTp"));
			} else if (allowedcmd.equalsIgnoreCase("id")) {
				p.sendMessage(" /plotme id");
				p.sendMessage(" " + caption("HelpId"));
			} else if (allowedcmd.equalsIgnoreCase("clear")) {
				p.sendMessage(" /plotme " + caption("CommandClear"));
				if (ecoon && pmi != null && pmi.ClearPrice != 0) {
					p.sendMessage(" " + caption("HelpId") + " Price : " + round(pmi.ClearPrice));
				} else {
					p.sendMessage(" " + caption("HelpClear"));
				}
			} else if (allowedcmd.equalsIgnoreCase("reset")) {
				p.sendMessage(" /plotme " + caption("CommandReset"));
				p.sendMessage(" " + caption("HelpReset"));
			} else if (allowedcmd.equalsIgnoreCase("add")) {
				p.sendMessage(" /plotme " + caption("CommandAdd") + " <Player>");
				if (ecoon && pmi != null && pmi.AddPlayerPrice != 0) {
					p.sendMessage(" " + caption("HelpAdd") + " Price : " + round(pmi.AddPlayerPrice));
				} else {
					p.sendMessage(" " + caption("HelpAdd"));
				}
			} else if (allowedcmd.equalsIgnoreCase("deny")) {
				p.sendMessage(" /plotme deny <Player>");
				if (ecoon && pmi != null && pmi.DenyPlayerPrice != 0) {
					p.sendMessage(" " + caption("HelpDeny") + " Price : " + round(pmi.DenyPlayerPrice));
				} else {
					p.sendMessage(" " + caption("HelpDeny"));
				}
			} else if (allowedcmd.equalsIgnoreCase("remove")) {
				p.sendMessage(" /plotme " + caption("CommandRemove") + " <Player>");
				if (ecoon && pmi != null && pmi.RemovePlayerPrice != 0) {
					p.sendMessage(" " + caption("HelpRemove") + " Price : " + round(pmi.RemovePlayerPrice));
				} else {
					p.sendMessage(" " + caption("HelpRemove"));
				}
			} else if (allowedcmd.equalsIgnoreCase("undeny")) {
				p.sendMessage(" /plotme undeny <Player>");
				if (ecoon && pmi != null && pmi.UndenyPlayerPrice != 0) {
					p.sendMessage(" " + caption("HelpUndeny") + " Price : " + round(pmi.UndenyPlayerPrice));
				} else {
					p.sendMessage(" " + caption("HelpUndeny"));
				}
			} else if (allowedcmd.equalsIgnoreCase("setowner")) {
				p.sendMessage(" /plotme " + caption("CommandSetowner") + " <Player>");
				p.sendMessage(" " + caption("HelpSetowner"));
			} else if (allowedcmd.equalsIgnoreCase("move")) {
				p.sendMessage(" /plotme move <id-from> <id-to>");
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
					p.sendMessage(" " + caption("HelpDispose") + " Price : " + round(pmi.DisposePrice));
				} else {
					p.sendMessage(" " + caption("HelpDispose"));
				}
			} else if (allowedcmd.equalsIgnoreCase("buy")) {
				p.sendMessage(" /plotme buy");
				p.sendMessage(" " + caption("HelpBuy"));
			} else if (allowedcmd.equalsIgnoreCase("sell")) {
				p.sendMessage(" /plotme " + caption("CommandSell") + " [amount]");
				p.sendMessage(" " + caption("HelpSell") + " Default : " + round(pmi.SellToPlayerPrice));
			} else if (allowedcmd.equalsIgnoreCase("sellbank")) {
				p.sendMessage(" /plotme " + caption("CommandSellBank"));
				p.sendMessage(" " + caption("HelpSellBank") + " " + round(pmi.SellToBankPrice));
			} else if (allowedcmd.equalsIgnoreCase("auction")) {
				p.sendMessage(" /plotme auction [amount]");
				p.sendMessage(" " + caption("HelpAuction") + " Default : 1");
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
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				if (args.length == 2) {
					String id = args[1];

					if (!PlotManager.isValidId(id)) {
						p.sendMessage("Usage: /plotme tp <id>");
						p.sendMessage("Example: /plotme tp 5;-1 ");
						return false;
					}
					World world = Bukkit.getWorld("plotworld");

					if (world == null) {
						p.sendMessage(caption("MsgNoPlotworldFound"));
					} else {
						Location bottom = PlotManager.getPlotBottomLoc(world, id);
						Location top = PlotManager.getPlotTopLoc(world, id);

						p.teleport(new Location(world, bottom.getX() + (top.getBlockX() - bottom.getBlockX()) / 2, PlotManager.getMap(world).RoadHeight + 2, bottom.getZ() - 2));
					}
				} else {
					p.sendMessage("Usage: /plotme tp <id> Example: /plotme tp 5;-1 ");
				}
			} else if (args.length == 2) {
				String id = args[1];

				if (!PlotManager.isValidId(id)) {
					p.sendMessage("Usage: /plotme tp <id> Example: /plotme tp 5;-1 ");
					return false;
				}
				World w = p.getWorld();

				if (w == null || !(w == Bukkit.getWorld("plotworld"))) {
					p.sendMessage(caption("MsgNoPlotworldFound"));
					return false;
				} else {
					Location bottom = PlotManager.getPlotBottomLoc(w, id);
					Location top = PlotManager.getPlotTopLoc(w, id);

					p.teleport(new Location(w, bottom.getX() + (top.getBlockX() - bottom.getBlockX()) / 2, PlotManager.getMap(w).RoadHeight + 2, bottom.getZ() - 2));
				}
			} else {
				p.sendMessage("Usage: /plotme tp <id> Example: /plotme tp 5;-1 ");
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean auto(Player p) {
		if (p.hasPermission("PlotMe.use.auto")) {
			World w = Bukkit.getWorld("plotworld");

			if (w == null) {
				p.sendMessage(caption("MsgNoPlotworldFound"));
				return false;
			}
			if (PlotManager.getNbOwnedPlot(p, w) >= getPlotLimit(p) && !p.hasPermission("PlotMe.admin")) {
				p.sendMessage(caption("MsgAlreadyReachedMaxPlots") + " (" +
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
											p.sendMessage(er.errorMessage);
											warn(er.errorMessage);
											return false;
										}
									} else {
										p.sendMessage(caption("MsgNotEnoughAuto") + " Missing " + f(price - balance, false));
										return false;
									}
								}

								Plot plot = PlotManager.createPlot(w, id, name, uuid);

								//PlotManager.adjustLinkedPlots(id, w);

								p.teleport(new Location(w, PlotManager.bottomX(plot.id, w) + (PlotManager.topX(plot.id, w) -
										PlotManager.bottomX(plot.id, w)) / 2, pmi.RoadHeight + 2, PlotManager.bottomZ(plot.id, w) - 2));

								p.sendMessage(caption("MsgThisPlotYours") + " Use /plotme home " + caption("MsgToGetToIt") + " " + f(-price));

								logger.info("[Event] " + name + " " + caption("MsgClaimedPlot") + " " + id + ((price != 0) ? " for " + price : ""));

								return true;
							}
						}
					}
				}

				p.sendMessage(caption("MsgNoPlotFound1") + " " + (limit ^ 2) + " " + caption("MsgNoPlotFound2"));
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean claim(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.claim") || p.hasPermission("PlotMe.admin.claim.other")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					p.sendMessage(caption("MsgCannotClaimRoad"));
				} else if (!PlotManager.isPlotAvailable(id, p)) {
					p.sendMessage(caption("MsgThisPlotOwned"));
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
						p.sendMessage(caption("MsgAlreadyReachedMaxPlots") + " (" +
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
									p.sendMessage(er.errorMessage);
									warn(er.errorMessage);
									return true;
								}
							} else {
								p.sendMessage(caption("MsgNotEnoughBuy") + " Missing " + (price - balance) + " " + economy.currencyNamePlural());
								return true;
							}
						}

						Plot plot = PlotManager.createPlot(w, id, playername, uuid);

						//PlotManager.adjustLinkedPlots(id, w);

						if (plot == null) {
							p.sendMessage(caption("ErrCreatingPlotAt") + " " + id);
						} else {
							if (playername.equalsIgnoreCase(p.getName())) {
								p.sendMessage(caption("MsgThisPlotYours") + " Use /plotme home " + caption("MsgToGetToIt") + " " + f(-price));
							} else {
								p.sendMessage("This plot now belongs to " + playername + ". Use /plotme home " + caption("MsgToGetToIt") + " " + f(-price));
							}

							logger.info("[Event] " + playername + " " + caption("MsgClaimedPlot") + " " + id + ((price != 0) ? " for " + price : ""));
						}
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean home(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.home") || p.hasPermission("PlotMe.admin.home.other")) {
			String playername = p.getName();
			UUID uuid = p.getUniqueId();
			int nb = 1;
			World w;
			String worldname = "";

			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				w = Bukkit.getWorld("plotworld");
			} else {
				w = p.getWorld();
			}

			if (w != null) {
				worldname = w.getName();
			}

			if (args[0].contains(":")) {
				try {
					if (args[0].split(":").length == 1 || args[0].split(":")[1].isEmpty()) {
						p.sendMessage("Usage: /plotme home:#");
						p.sendMessage("Example: /plotme home:1");
						return true;
					}
					nb = Integer.parseInt(args[0].split(":")[1]);
				} catch (NumberFormatException ex) {
					p.sendMessage("Usage: /plotme home:#");
					p.sendMessage("Example: /plotme home:1");
					return true;
				}
			}

			if (args.length == 2) {
				if (p.hasPermission("PlotMe.admin.home.other")) {
					playername = args[1];
					uuid = null;
				}
			}

			if (!(w == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(worldname + " " + caption("MsgWorldNotPlot"));
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
										p.sendMessage(er.errorMessage);
										return true;
									}
								} else {
									p.sendMessage(caption("MsgNotEnoughTp") + " Missing " + f(price - balance, false));
									return true;
								}
							}

							p.teleport(PlotManager.getPlotHome(w, plot));

							if (price != 0) {
								p.sendMessage(f(-price));
							}

							return true;
						}
						i--;
					}
				}

				if (nb > 0) {
					if (!playername.equalsIgnoreCase(p.getName())) {
						p.sendMessage(playername + " " + caption("MsgDoesNotHavePlot") + " #" + nb);
					} else {
						p.sendMessage(caption("MsgPlotNotFound") + " #" + nb);
					}
				} else if (!playername.equalsIgnoreCase(p.getName())) {
					p.sendMessage(playername + " " + caption("MsgDoesNotHavePlot"));
				} else {
					p.sendMessage(caption("MsgYouHaveNoPlot"));
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean info(Player p) {
		if (p.hasPermission("PlotMe.use.info")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());

				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						p.sendMessage(caption("InfoId") + ": " + id + " " + caption("InfoOwner") + ": " + plot.owner + " " + caption("InfoBiome") + ": " + FormatBiome(plot.biome.name()));

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
								if (plot.auctionned)
									if (plot.forsale)
										p.sendMessage(caption("InfoAuctionned") + ": " + ("Yes " + caption("InfoBidder") + ": " + plot.currentbidder +
												" Bid: " + round(plot.currentbid)) + " " + caption("InfoForSale") + ": " + round(plot.customprice));
									else
										p.sendMessage(caption("InfoAuctionned") + ": " + ("Yes " + caption("InfoBidder") + ": " + plot.currentbidder +
												" Bid: " + round(plot.currentbid)) + " " + caption("InfoForSale") + ": " + "No");
								else if (plot.forsale)
									p.sendMessage(caption("InfoAuctionned") + ": " + "No" + " " + caption("InfoForSale") + ": " + round(plot.customprice));
								else
									p.sendMessage(caption("InfoAuctionned") + ": " + "No" + " " + caption("InfoForSale") + ": " + "No");
							}
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean comment(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.comment")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				if (args.length < 2) {
					p.sendMessage("Usage: /plotme " + caption("CommandComment") + " <text>");
				} else {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						p.sendMessage(caption("MsgNoPlotFound"));
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
										p.sendMessage(er.errorMessage);
										warn(er.errorMessage);
										return true;
									}
								} else {
									p.sendMessage(caption("MsgNotEnoughComment") + " Missing " + f(price - balance, false));
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

							p.sendMessage(caption("MsgCommentAdded") + " " + f(-price));

							logger.info("[Event] " + playername + " " + caption("MsgCommentedPlot") + " " + id + ((price != 0) ? " for " + price : ""));
						} else {
							p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
						}
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean comments(Player p, String[] args) {
		if (p.hasPermission("PlotMe.use.comments")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				if (args.length < 2) {
					String id = PlotManager.getPlotId(p.getLocation());

					if (id.isEmpty()) {
						p.sendMessage(caption("MsgNoPlotFound"));
					} else {
						if (!PlotManager.isPlotAvailable(id, p)) {
							Plot plot = PlotManager.getPlotById(p, id);

							if (plot.ownerId.equals(p.getUniqueId()) || plot.isAllowed(p.getUniqueId()) || p.hasPermission("PlotMe.admin")) {
								if (plot.comments.isEmpty()) {
									p.sendMessage(caption("MsgNoComments"));
								} else {
									p.sendMessage(caption("MsgYouHave") + " " + plot.comments.size() + " " + caption("MsgComments"));

									for (String[] comment : plot.comments) {
										p.sendMessage("From : " + comment[0]);
										p.sendMessage("" + comment[1]);
									}

								}
							} else {
								p.sendMessage("This plot(" + id + ") " + caption("MsgNotYoursNotAllowedViewComments"));
							}
						} else {
							p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
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
			//If world isn't plotworld, return false.
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
				return false;
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
					return false;
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						World w = Bukkit.getWorld("plotworld");

						if (args.length == 2) {
							Biome biome = null;

							for (Biome bio : Biome.values()) {
								if (bio.name().equalsIgnoreCase(args[1])) {
									biome = bio;
								}
							}

							if (biome == null) {
								p.sendMessage(args[1] + " " + caption("MsgIsInvalidBiome"));
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
												p.sendMessage(er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											p.sendMessage(caption("MsgNotEnoughBiome") + " Missing " + f(price - balance, false));
											return true;
										}
									}

									PlotManager.setBiome(Bukkit.getWorld("plotworld"), id, plot, biome);

									p.sendMessage(caption("MsgBiomeSet") + " " + FormatBiome(biome.name()) + " " + f(-price));

									logger.info("[Event] " + playername + " " + caption("MsgChangedBiome") + " " + id + " to " +
											FormatBiome(biome.name()) + ((price != 0) ? " for " + price : ""));
								} else {
									p.sendMessage("This plot(" + id + ") " + caption("MsgNotYoursNotAllowedBiome"));
								}
							}
						} else {
							Plot plot = plotmaps.get(w.getName().toLowerCase()).plots.get(id);

							p.sendMessage(caption("MsgPlotUsingBiome") + " " + FormatBiome(plot.biome.name()));
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
			return false;
		}
		return true;
	}

	private boolean biomelist(Player player) {
		player.sendMessage("Biomes : ");
		//int i = 0;
		StringBuilder line = new StringBuilder();
		List<String> biomes = new ArrayList<>();

		for (Biome biome : Biome.values()) {
			biomes.add(biome.name());
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

			player.sendMessage("" + line);
			//i = 0;
			line = new StringBuilder();

		}
		return true;
	}

	private boolean reset(Player p) {
		if (p.hasPermission("PlotMe.admin.reset")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
				return false;
			} else {
				Plot plot = PlotManager.getPlotById(p.getLocation());

				if (plot == null) {
					p.sendMessage(caption("MsgNoPlotFound"));
					return false;
				} else {
					if (plot.protect) {
						p.sendMessage(caption("MsgPlotProtectedCannotReset"));
					} else {
						String id = plot.id;
						World world = Bukkit.getWorld("plotworld");

						PlotManager.setBiome(Bukkit.getWorld("plotworld"), id, plot, Biome.PLAINS);
						PlotManager.clear(world, plot);

						if (PlotManager.isEconomyEnabled(p)) {
							if (plot.auctionned) {
								String currentbidder = plot.currentbidder;

								if (!currentbidder.isEmpty()) {
									EconomyResponse er = economy.depositPlayer(currentbidder, plot.currentbid);

									if (!er.transactionSuccess()) {
										p.sendMessage(er.errorMessage);
										warn(er.errorMessage);
									} else {
										for (Player player : Bukkit.getServer().getOnlinePlayers()) {
											if (player.getName().equalsIgnoreCase(currentbidder)) {
												player.sendMessage("Plot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgWasReset") + " " + f(plot.currentbid));
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
									p.sendMessage(er.errorMessage);
									warn(er.errorMessage);
									return true;
								} else {
									for (Player player : Bukkit.getServer().getOnlinePlayers()) {
										if (player.getName().equalsIgnoreCase(plot.owner)) {
											player.sendMessage("Plot " + id + " " + caption("MsgOwnedBy") + " " + plot.owner + " " + caption("MsgWasReset") + " " + f(pmi.ClaimPrice));
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

						PlotManager.removeOwnerSign(world, id);
						PlotManager.removeSellSign(world, id);

						SqlManager.deletePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), world.getName().toLowerCase());

						p.sendMessage(caption("MsgPlotReset"));

						logger.info("[Event] " + name + " " + caption("MsgResetPlot") + " " + id);
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean clear(Player p) {
		if (p.hasPermission("PlotMe.admin.clear") || p.hasPermission("PlotMe.use.clear")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						Plot plot = PlotManager.getPlotById(p, id);

						if (plot.protect) {
							p.sendMessage(caption("MsgPlotProtectedCannotClear"));
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
											p.sendMessage(er.errorMessage);
											warn(er.errorMessage);
											return true;
										}
									} else {
										p.sendMessage(caption("MsgNotEnoughClear") + " Missing " + (price - balance) + " " + economy.currencyNamePlural());
										return true;
									}
								}

								PlotManager.clear(w, plot);
								//RemoveLWC(w, plot, p);
								//PlotManager.regen(w, plot);

								p.sendMessage(caption("MsgPlotCleared") + " " + f(-price));

								logger.info("[Event] " + playername + " " + caption("MsgClearedPlot") + " " + id + ((price != 0) ? " for " + price : ""));
							} else {
								p.sendMessage("This plot(" + id + ") " + caption("MsgNotYoursNotAllowedClear"));
							}
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean add(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.add") || p.hasPermission("PlotMe.use.add")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							p.sendMessage("Usage /plotme " + caption("CommandAdd") + " <Player>");
						} else {

							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();
							String allowed = args[1];

							if (plot.owner.equalsIgnoreCase(playername) || p.hasPermission("PlotMe.admin.add")) {
								if (plot.isAllowedConsulting(allowed) || plot.isGroupAllowed(allowed)) {
									p.sendMessage("Player " + args[1] + " " + caption("MsgAlreadyAllowed"));
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
												p.sendMessage(er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											p.sendMessage(caption("MsgNotEnoughAdd") + " Missing " + f(price - balance, false));
											return true;
										}
									}

									plot.addAllowed(allowed);
									plot.removeDenied(allowed);

									p.sendMessage("Player " + allowed + " " + caption("MsgNowAllowed") + " " + f(-price));

									logger.info("[Event] " + playername + " " + caption("MsgAddedPlayer") + " " + allowed + " " + caption("MsgToPlot") + " " + id + ((price != 0) ? " for " + price : ""));
								}
							} else {
								p.sendMessage("This plot(" + id + ") " + caption("MsgNotYoursNotAllowedAdd"));
							}
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean deny(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.deny") || p.hasPermission("PlotMe.use.deny")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							p.sendMessage("Usage /plotme deny <Player>");
						} else {

							Plot plot = PlotManager.getPlotById(p, id);
							String playername = p.getName();
							String denied = args[1];

							if (plot.owner.equalsIgnoreCase(playername) || p.hasPermission("PlotMe.admin.deny")) {
								if (plot.isDeniedConsulting(denied) || plot.isGroupDenied(denied)) {
									p.sendMessage("Player " + args[1] + " " + caption("MsgAlreadyDenied"));
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
												p.sendMessage(er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											p.sendMessage(caption("MsgNotEnoughDeny") + " Missing " + f(price - balance, false));
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

									p.sendMessage("Player " + denied + " " + caption("MsgNowDenied") + " " + f(-price));

									logger.info("[Event] " + playername + " " + caption("MsgDeniedPlayer") + " " + denied + " " + caption("MsgToPlot") + " " + id + ((price != 0) ? " for " + price : ""));
								}
							} else {
								p.sendMessage("This plot(" + id + ") " + caption("MsgNotYoursNotAllowedDeny"));
							}
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean remove(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.remove") || p.hasPermission("PlotMe.use.remove")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							p.sendMessage("Usage: /plotme " + caption("CommandRemove") + " <Player>");
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
												p.sendMessage(er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											p.sendMessage(caption("MsgNotEnoughRemove") + " Missing " + f(price - balance, false));
											return true;
										}
									}

									if (allowed.startsWith("group:")) {
										plot.removeAllowedGroup(allowed);
									} else {
										plot.removeAllowed(allowed);
									}

									p.sendMessage("Player " + allowed + " removed. " + f(-price));

									logger.info("[Event] " + p.getName() + " " + caption("MsgRemovedPlayer") + " " + allowed + " " + caption("MsgFromPlot") + " " + id + ((price != 0) ? " for " + price : ""));
								} else {
									p.sendMessage("Player " + args[1] + " " + caption("MsgWasNotAllowed"));
								}
							} else {
								p.sendMessage("This plot(" + id + ") " + caption("MsgNotYoursNotAllowedRemove"));
							}
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean undeny(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.undeny") || p.hasPermission("PlotMe.use.undeny")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (!PlotManager.isPlotAvailable(id, p)) {
						if (args.length < 2 || args[1].equalsIgnoreCase("")) {
							p.sendMessage("Usage: /plotme undeny <Player>");
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
												p.sendMessage(er.errorMessage);
												warn(er.errorMessage);
												return true;
											}
										} else {
											p.sendMessage(caption("MsgNotEnoughUndeny") + " Missing " + f(price - balance, false));
											return true;
										}
									}

									if (denied.startsWith("group:")) {
										plot.removeDeniedGroup(denied);
									} else {
										plot.removeDenied(denied);
									}

									p.sendMessage("Player " + denied + " " + caption("MsgNowUndenied") + " " + f(-price));

									logger.info("[Event] " + playername + " " + caption("MsgUndeniedPlayer") + " " + denied + " " + caption("MsgFromPlot") + " " + id + ((price != 0) ? " for " + price : ""));
								} else {
									p.sendMessage("Player " + args[1] + " " + caption("MsgWasNotDenied"));
								}
							} else {
								p.sendMessage("This plot(" + id + ") " + caption("MsgNotYoursNotAllowedUndeny"));
							}
						}
					} else {
						p.sendMessage("This plot(" + id + ") " + caption("MsgHasNoOwner"));
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean setowner(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.setowner")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				String id = PlotManager.getPlotId(p.getLocation());
				if (id.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					if (args.length < 2 || args[1].isEmpty()) {
						p.sendMessage("Usage: /plotme " + caption("CommandSetowner") + " <Player>");
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
										p.sendMessage(er.errorMessage);
										warn(er.errorMessage);
										return true;
									} else {
										for (Player player : Bukkit.getServer().getOnlinePlayers()) {
											if (player.getName().equalsIgnoreCase(oldowner)) {
												player.sendMessage(caption("MsgYourPlot") + " " + id + " " + caption("MsgNowOwnedBy") + " " + newowner + ". " + f(pmi.ClaimPrice));
												break;
											}
										}
									}
								}

								if (plot.currentbidder != null && !plot.currentbidder.isEmpty()) {
									EconomyResponse er = economy.depositPlayer(plot.currentbidder, plot.currentbid);

									if (!er.transactionSuccess()) {
										p.sendMessage(er.errorMessage);
										warn(er.errorMessage);
									} else {
										for (Player player : Bukkit.getServer().getOnlinePlayers()) {
											if (player.getName().equalsIgnoreCase(plot.currentbidder)) {
												player.sendMessage("Plot " + id + " " + caption("MsgChangedOwnerFrom") + " " + oldowner + " to " + newowner + ". " + f(plot.currentbid));
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

						p.sendMessage(caption("MsgOwnerChangedTo") + " " + newowner);

						logger.info("[Event] " + playername + " " + caption("MsgChangedOwnerOf") + " " + id + " From " + oldowner + " to " + newowner);
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean id(Player p) {
		if (p.hasPermission("PlotMe.admin.id")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				String plotid = PlotManager.getPlotId(p.getLocation());

				if (plotid.isEmpty()) {
					p.sendMessage(caption("MsgNoPlotFound"));
				} else {
					p.sendMessage("Plot id: " + plotid);

					Location bottom = PlotManager.getPlotBottomLoc(p.getWorld(), plotid);
					Location top = PlotManager.getPlotTopLoc(p.getWorld(), plotid);

					p.sendMessage("Bottom: " + bottom.getBlockX() + "," + bottom.getBlockZ());
					p.sendMessage("Top: " + top.getBlockX() + "," + top.getBlockZ());
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean move(Player p, String[] args) {
		if (p.hasPermission("PlotMe.admin.move")) {
			if (!(p.getWorld() == Bukkit.getWorld("plotworld"))) {
				p.sendMessage(caption("MsgNotPlotWorld"));
			} else {
				if (args.length < 3 || args[1].equalsIgnoreCase("") || args[2].equalsIgnoreCase("")) {
					p.sendMessage("Usage: /plotme move <id-from> <id-to> " +
							"Example: /plotme move 0;1 2;-1");
				} else {
					String plot1 = args[1];
					String plot2 = args[2];

					if (!PlotManager.isValidId(plot1) || !PlotManager.isValidId(plot2)) {
						p.sendMessage("Usage: /plotme move <id-from> <id-to> " +
								"Example: /plotme move 0;1 2;-1");
						return true;
					} else {
						if (PlotManager.movePlot(p.getWorld(), plot1, plot2)) {
							p.sendMessage(caption("MsgPlotMovedSuccess"));

							logger.info("[Event] " + p.getName() + " " + caption("MsgExchangedPlot") + " " + plot1 + " " + caption("MsgAndPlot") + " " + plot2);
						} else {
							p.sendMessage(caption("ErrMovingPlot"));
						}
					}
				}
			}
		} else {
			p.sendMessage(caption("MsgPermissionDenied"));
		}
		return true;
	}

	private boolean reload(CommandSender sender) {
		if (!(sender instanceof Player) || sender.hasPermission("PlotMe.admin.reload")) {
			plugin.initialize();
			sender.sendMessage(caption("MsgReloadedSuccess"));

			logger.info("[Event] " + sender.getName() + " " + caption("MsgReloadedConfigurations"));
		} else {
			sender.sendMessage(caption("MsgPermissionDenied"));
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
		logger.warning("[Event] " + msg);
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