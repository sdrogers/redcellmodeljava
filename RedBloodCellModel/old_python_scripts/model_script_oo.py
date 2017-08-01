from __future__ import division
import json
import sys
import math
from simon_listener import Listener
from cell_components import region,species,napump,JacobsStewart,Cotransport,CarrierMediated,Goldman,A23187,WaterFlux,PassiveCa,CaPumpMg2
# '"M1":'Red Cell Model
# 'Last update: Cambridge, September 2008
# 'Incorporates:
# '09/08: Reversibility of Clxgluconate exchange
# '02/00: Clinton Joiner's suggestions, comments and bug-findings:
# '	-Gardos inhibition
# '	-Ca pump stoichiometry: Ca:2H electroneutral option
# '	-Redundancy in definition of fcaph
# '	-Big bug associated with fcal; debugged 05/02/00 as follows:
# '	Changed "+fcal" to "+2*fcal" in
# '		7730 Y(6)=I(22)+E(3)+F(11)+F(12)-F(13)+cah*fcap+2*fcal
# '	(in Newton-Raphson routine for computation of the membrane potential)
# '	Turns out that the misshandling of fcal was the major source of
# '	cumulative errors. We now have much higher precision in EN-tests.
# '	-New Na-pump screens in RS and transients, to model steady-states
# '	with modified Na-pump parameters and constant leaks.  This follows
# '	requests by Joe Hoffman and Clint Joiner, and suggestions by Clint.
# '19/1/00: A Na-pump RS-properties screen was added on Joe Hoffman's request
# '7/4/98: Tom Weiss expression of Goldman flux equations: 'G-fluxes.
# '	Note that since net fluxes into the cell were defined positive
# '	the sign of all ion and tracer G-fluxes was reversed.
# '	The results are identical to those obtained with previous
# '	model version.
# '8/4/98 Added suggestion on how to simulate hrbc ghosts in RS state
# 'Corrected Ca pump and KCa channel parameters
# 'Choice of single anionic or cationic H-buffers (enter pKa) for post RS
# '	following Thomas Fischer request.
# 'pKa of HEPES endowed with temperature-dependence: "pkhepes".
# 'Debugged M1BUGGED: cat was too high for new cabuffs; caused havoc in canr.
# 'Must remember to match initial caf-cat-fcal values whenever changed
# '	following Thomas Fischer finding of bug.
# 'Variable Hb content option (MCHC prompt)
# '	following Carlo Brugnara's request
# 'Temporary (1/95) correction of non-balanced RS fluxes of A and H: "fal".
# 'pI change secondary to temperature changes from RS condition.
# 'Improved reporting of tracer efflux
# 'H+ displacements when EDTA/EGTA bind Ca or Mg.
# 'Chelator binding constants from the Schoenmakers Chelator Programme.
# 'Option to add additional external buffers and M2+ chelators (EDTA/EGTA);
# ' this required solution of proton, Ca2+ and Mg2+ concentrations in system
# ' with multiple proton buffers and M2+ chelators in the external medium, with
# ' consideration of the water and proton shifts between cells and medium.  It
# ' was crucial to work out a rapid solution method because otherwise
# ' computation of the H+, Ca2+ and Mg2+ concentrations within each iteration
# ' could considerably slow down the running of the model.
# 'Mg-dependence of Na and Ca pumps
# 'Inhibition of the Ca pump by internal pH.
# 'Q(4), the titratable internal impermeant ion is redefined to Q(4)=fHb*QHb
# 'Ca:2H(A23187), PCaL, Ca pump, Ca-activated PK, cytoplsmic Ca buffering and
# 'Ca/Mg competition on A23187.
# 'Mg:2H(A23187) and cytoplasmic Mg buffering (with Julia Raftos, July, 1993).
# 'Cytoplasmic Mg and Ca buffers are assumed to be within X (the impermeant
# 'cell ion) and are therefore not computed with separate contributions to
# 'osmolality and charge.  Therefore only the M2+ forms contribute tonicity.
# 'Input of RS cell water option.
# 'pH-dependence of Na pump: exp((-pHc-i(77))/i(78))^2.
# 'Temperature-dependence of pI. RS options for [A]c, `a' and pI(T=0oC).
# 'Tracer efflux option on second experimental period.
# 'Carriers are redefined as electroneutral Na:Cl and K:Cl cotransports.
# 'This generates a minor departure of E from ECl. Lines: 1530-70,6690,7830.
# 'Format with .RES, .INT and .ASC results-files.
# 'BASED on RED CELL MODEL. LEW & BOOKCHIN, J.Membrane Biol.,1986,92:57-74.
# 'ORIGINAL BASIC VERSION (for EPSON PX8): BUENOS AIRES, MAY 1986, BY
# 'VIRGILIO L. LEW, PHYSIOLOGICAL LABORATORY, CAMBRIDGE UNIVERSITY, UK.
# 'The numerical solution of the equation for membrane potential, E,
# 'is computed by the Newton-Raphson method using cord-approximations
# 'instead of tangents. This avoids the need to operate with analytical
# 'expressions for the first derivative of the implicit E-function.
# 'Extensions and updates were done using compiled Turbo and Power BASIC versions.
# 'Sections not updated were generally left unmodified.



class rbc_model(object):

	def __init__(self):

		self.debug = False

		self.cell = region()
		self.medium = region()
		self.napump = napump(self.cell,self.medium)
		self.JS = JacobsStewart(self.cell,self.medium)
		self.cotransport = Cotransport(self.cell,self.medium)
		self.carriermediated = CarrierMediated(self.cell,self.medium)
		self.goldman = Goldman(self.cell,self.medium)
		self.a23 = A23187(self.cell,self.medium)
		self.water = WaterFlux(self.cell,self.medium)
		self.passiveCa = PassiveCa(self.cell,self.medium,self.goldman)
		self.capump = CaPumpMg2(self.cell,self.medium,self.napump)
		self.A_1 = -10.0
		self.A_2 = 0.0645
		self.A_3 = 0.0258
		# self.A_4 = 7.2
		self.pit0 = 7.2
		self.A_5 = 2.813822508658947e-08
		self.integration_interval_factor = 0.01 # Integration factor
		self.A_7 = 0.0
		self.A_8 = 0.0
		# self.A_9 = 0.0
		self.hb_content = 34.0
		self.A_10 = 0.0
		self.A_11 = 0.0
		self.A_12 = 0.0

		# self.B_1 = 0.2
		# self.B_2 = 18.0
		# self.B_3 = 0.1
		# self.B_4 = 8.3
		# self.B_5 = 0.0
		# fG is proportion of flux going through G (B_6) - only used in RS
		self.fG = 0.1
		# B_7 is due to the ratio in the Na pump - for every 3 Na out, 2 K come in (B_7)
		self.Na_to_K = 1.5
		# self.B_8 = 37.0
		self.temp_celsius = 37.0
		# self.B_9 = 4.0
		self.B_10 = 2.0

		# self.C_1 = 10.0
		self.cell.Na.concentration = 10.0
		# self.C_2 = 140.0
		self.cell.K.concentration = 140.0
		# self.C_3 = 95.0
		self.cell.A.concentration = 95.0
		# self.C_4 = 0.0
		self.cell.H.concentration = 0.0
		# self.C_5 = 0.0
		self.cell.Hb.concentration = 0.0
		# self.C_6 = 0.0
		self.cell.X.concentration = 0.0
		# self.C_7 = 0.0
		self.cell.Mgt.concentration = 0.0
		self.cell.XHbm.concentration = 0.0
		# self.C_9 = 0.0
		# self.cell.COs.concentration
		# self.C_10 = 0.0
		# self.cell.Hbpm.concentration

		# self.D_4 = 0.0
		self.delta_H = 0.0
		self.D_6 = 0.001
		self.D_12 = 0.0001

		# self.E_3 = 0.0
		# self.flux_Goldman_H = 0.0
		# self.E_8 = 2e-10
		# self.permeability_Goldman_H = 2e-10
		# self.E_17 = 0.0
		# self.flux_water = 0.0

		# self.F_1 = 0.0
		self.total_flux_Na = 0.0
		# self.F_2 = 0.0
		self.total_flux_K = 0.0
		# self.F_3 = 0.0
		self.total_flux_A = 0.0
		# self.F_4 = 0.0
		self.total_flux_H = 0.0
		# self.F_5 = -2.61
		# self.napump.flux_fwd = -2.61
		self.napmaxf = 1
		# self.F_6 = 0.0015
		self.napump.flux_rev = 0.0015
		self.napmaxr = 1
		# self.F_7 = 0.0
		# self.napump.flux_net = 0.0
		# self.F_8 = 0.0
		# self.total_flux_KP = 0.0
		# self.F_9 = 0.0
		# self.flux_Na_Cl = 0.0
		# self.flux_K_Cl = 0.0
		# self.flux_K_Cl = 0.0
		# self.F_11 = 0.0
		# self.flux_Goldman_Na = 0.0
		# self.F_12 = 0.0
		# self.flux_Goldman_K = 0.0
		# self.F_13 = 0.0
		# self.flux_Goldman_A = 0.0
		# self.F_14 = 0.0
		# self.flux_NaK2Cl_A = 0.0
		# self.F_15 = 0.0
		# self.flux_NaK2Cl_Na = 0.0
		# self.F_16 = 0.0
		# self.flux_NaK2Cl_K = 0.0
		# self.F_17 = 0.0
		# self.flux_JS_A = 0.0
		# self.F_18 = 0.0
		# self.flux_JS_H = 0.0

		# self.I_1 = 0.0
		# self.I_2 = 0.0
		self.I_3 = 0.0
		# self.I_4 = 0.0
		# self.I_5 = 0.0
		self.I_6 = 0.0
		# self.I_7 = 0.0
		# self.I_8 = 0.0
		self.I_9 = 0.0
		# self.I_10 = 0.0
		self.I_11 = 0.0
		self.I_12 = 0.0
		# self.I_13 = 0.0
		# self.I_14 = 0.0
		# self.Goldman_factor = 0.0

		# Following two variables removed through refactoring gfluxk and gfluxna methods
		# self.I_19 = 0.0
		# self.I_20 = 0.0
		
		# self.I_22 = 0.0
		# self.I_17 = 0.0
		self.I_18 = 0.0
		# self.I_25 = 0.0
		# self.cell_concentration_Os = 0.0
		# self.I_27 = 0.0
		# self.I_28 = 0.0
		# self.I_30 = 0.0

		self.I_73 = 0.0
		# These are used to calculate the ph of the sodium pump?
		self.I_77 = 7.216
		self.I_78 = 0.4
		self.I_79 = 0.75

		# self.M_1 = 145.0
		self.medium.Na.concentration = 145.0
		# self.M_2 = 5.0
		self.medium.K.concentration = 5.0
		# self.M_3 = 145.0
		self.medium.A.concentration = 145.0
		# self.M_4 = 0.0
		self.medium.H.concentration = 0.0
		# self.M_5 = 10.0
		self.buffer_conc = 10.0
		# self.M_6 = 0.0
		self.medium.Hb.concentration = 0.0

		# self.Q_1 = 0.0
		self.cell.Na.amount = 0.0
		# self.Q_2 = 0.0
		self.cell.K.amount = 0.0
		# self.Q_3 = 0.0
		self.cell.A.amount = 0.0
		self.Q_4 = 0.0
		# self.Q_5 = 0.0
		self.cell.Hb.amount = 0.0
		# self.Q_6 = 0.0
		self.cell.X.amount = 0.0
		# self.Q_7 = 0.0
		self.cell.Mgt.amount = 0.0

		# Q_8 only appears on the LHS of statements!
		self.Q_8 = 0.0
		# self.Q_9 = 0.0

		self.R = 0

		self.sampling_time = 0.0
		# self.T_2 = 0.0
		self.cycle_count = 0.0
		self.cycles_per_print = 777
		self.duration_experiment = 0.0
		# self.T_5 = 0.0
		self.n_its = 0.0
		self.T_6 = 0.0

		# self.V_1 = 0.0
		self.Vw = 0.0
		# self.V_2 = 0.0
		self.VV = 0.0
		# self.V_3 = 0.0
		self.fraction = 0.0
		# self.V_4 = 0.0
		self.mchc = 0.0
		# self.V_5 = 0.0
		self.density = 0.0
		# self.V_6 = 0.0
		self.Em = 0.0
		# self.V_7 = 0.0
		self.rA = 0.0
		# self.V_8 = 0.0
		self.rH = 0.0
		# self.V_9 = 0.0
		self.fHb = 0.0
		# self.V_10 = 0.0
		self.nHb = 0.0
		# self.V_11 = 0.0
		# self.internal_ph = 0.0
		# self.V_12 = 0.0
		# self.external_ph = 0.0

		self.X_6 = 0.0
		# self.Y_6 = 0.0
		self.total_flux = 0.0


		self.diff2 = 0.00001
		self.diff3 = 0.00001

		self.dedgh = 0.0
		self.ligchoice = 0.0
		self.edghk1 = 0.0
		self.edghk2 = 0.0
		self.edgcak = 0.0
		self.edgmgk = 0.0
		self.edg4 = 0.0
		self.edg3 = 0.0
		self.edg2 = 0.0
		self.edgmg = 0.0
		self.edgca = 0.0
		self.edgto = 0.0
		self.edgneg = 0.0
		self.edghnew = 0.0
		self.edghold = 0.0

		# self.fmg23 = 0.0
		# self.fca23 = 0.0
		# self.a23camk = 0.0
		# self.a23mgmk = 0.0
		# self.a23caik = 0.0
		# self.a23mgik = 0.0


		self.mgb = 0.0
		self.mgb0 = 0.0
		
		self.mgcao = 0.0
		self.mgcai = 0.0

		self.cell.Mgt.amount = 2.5

		self.camgi = 0.0
		self.camgo = 0.0
		# self.calreg  = 0.0
		# self.calik = 0.0
		# self.calk = 0.0
		# self.cah = 1.0
		# self.capk = 0.0
		# self.capmgk = 0.0
		# self.capmgki = 0.0
		self.cab = 0.0
		self.cabb = 0.0
		self.cell.Cat.concentration = 0.0

		
		self.total_flux_Ca = 0.0
		# self.fcalm = 0.0
		# self.fcapm = 0.0
		# self.fcap = 0.0
		# self.fcaph = 0.0



		self.pka = 0.0
		self.pkhepes = 0.0


		self.benz2 = 0.0
		self.benz2k = 0.0
		self.cbenz2 = 0.0

		self.b1ca = 0.0
		self.b1cak = 0.0
		self.alpha = 0.0
		# self.h1 = 0.0
		# self.hik = 0.0


		self.atp = 1.2
		self.dpgp = 0.0
		# self.phnap = 0.0
		self.BufferType = "HEPES"
		self.vlysis = 1.45
		# is gca ever used?
		self.gca = 0.0
		self.ff = 0.0


		self.delta_Mg = 0.0
		self.delta_Ca = 0.0

		self.observers = []

		# Order in csv file
		self.publish_order = ["Time","V/V","Vw","Hct","Em","pHi","pHo","MCHC",
		"Density","QNa","QK","QA","QCa","QMg","CNa","CK","CA","CCa2+","CMg2+",
		"CHb","CX","COs","rA","rH","fHb","nHb","MNa","MK","MA","FNaP","FCaP","FKP",
		"FNa","FK","FA","FH","FW","FNaG","FKG","FAG","FHG"]


		self.finished = False
		self.in_progress = False
		self.RS_computed = False
		self.time_set = False
		sl = Listener()
		self.register(sl)

		self.stage = 0

		self.buffers = {0:"HEPES",1:"A",2:"C"}

	def is_finished(self):
		return self.finished


	def is_in_progress(self):
		return self.in_progress

	def register(self,observer):
		self.observers.append(observer)

	def publish(self,name,value,time,stage):
		for i in self.observers:
			i.publish(name,value,time,stage=stage)

	def setup(self,rsoptions,used_options = []):
		# This method is called by the web app to set the ref state
		print 
		print
		print
		print 
		print
		print
		print "******************************************************************"
		print "        COMPUTING REFERENCE STATE  "
		print "******************************************************************"


		self.napumpscreenRS(rsoptions,used_options)
		self.cellwaterscreenRS(rsoptions,used_options)
		self.cellanionprotonscreenRS(rsoptions,used_options)
		self.chargeandpiscreenRS(rsoptions,used_options)
		

		self.cycles_per_print = 777
		self.Vw = self.I_79
		self.fraction = 0.000001
		self.medium.pH = 7.4
		self.A_12 = self.medium.pH
		self.A_11 = 1.0-self.hb_content/136.0
		self.R = 1
		self.sampling_time = 0

		self.setmgdefaults()
		self.setcadefaults()
		
		self.mgbufferscreenRS(rsoptions,used_options)
		self.cabufferscreenRS(rsoptions,used_options)


		self.check_options(rsoptions,used_options)

		# *********************************************************
		# Reference state computation
		# *********************************************************
		self.computeRS()

		# Tidy these up!
		self.R = 0.0
		self.cycle_count = 0.0
		self.n_its = 0.0
		self.Z = 0.0
		self.duration_experiment = 0.0

		self.RS_computed = True

		# Set stage to zero everytime the RS is computed - stage = 1 means we're about to start DS 1
		self.stage = 1

		# self.publish_all()

	def get_stage(self):
		return self.stage

	def is_RS_computed(self):
		return self.RS_computed
		
	def setupds(self,options,used_options = []):

		print
		print
		print
		print
		print "**********************************************************************"
		print "           SETTING UP DYNAMIC STATE"
		print "**********************************************************************"
		self.set_screen_time_factor_options(options,used_options)
		self.set_cell_fraction_options(options,used_options)
		self.set_transport_changes_options(options,used_options)
		self.set_temp_permeability_options(options,used_options)
		
		self.check_options(options,used_options)
		# self.publish_all()

	def check_options(self,options,used_options):
		# Checks to see which options in options aren't in used options
		print
		print
		print "*************************"
		print "Checking All Options Used"
		print "*************************"
		unused_count = 0
		for i in options:
			if not i in used_options:
				print "WARNING: UNUSED OPTION: {}".format(i)
				unused_count += 1
		if unused_count == 0:
			print "ALL OPTIONS USED"
		print
		print


	def runall(self):	
		print
		print
		print
		print "*****************************************************"	
		print "         RUNNING MODEL"
		print "*****************************************************"	
		print
		print
		# **************************************
		# **************************************
		# **************************************
		# New na pump leak screen - to be implemented
		# New na pump leak steady state options - implement this!

		# if i(58)=6 then goto napparameters else 2830
		# napparameters:
		# if self.napmaxf=1 then p(1)=11.4
		# if self.napmaxr=1 then p(2)=11.8
		# p(3)=1.15e-4
		# p(5)=1.25e-4
		# i(58)=0
		# goto 2850

		# 2830 GOTO 2370
		# **************************************
		# **************************************
		# **************************************


		# **************************************
		# Start the main iterative loop
		# **************************************
		if self.debug == True:
			self.systemparoutput()

		if self.sampling_time*60 > self.duration_experiment:
			# this means that were trying to run without increasing time
			print "Can't run model for 0 time!!"
			return
		
		self.finished = False
		self.in_progress = True
		# mainloopfinished = False


		# resets the count to the next printout
		self.cycle_count = 0
		self.n_its = 0.0
		self.Z = 0

		self.publish_all()

		# self.debug = True
		while self.sampling_time * 60 <= self.duration_experiment:
			# while mainloopfinished == False:

			# Put this code into another method that runs one step
			

			# Compute the fluxes that don't depends on the membrane potential
			# compute the napump fluxes
			self.napump.compute_flux(self.temp_celsius)
			# Temperature-dependence of passive fluxes
			self.I_18 = math.exp(((37.0-self.temp_celsius)/10.0)*math.log(self.B_10))
			self.carriermediated.compute_flux(self.I_18)
			self.cotransport.compute_flux(self.I_18)
			self.JS.compute_flux(self.I_18)
			
			# Compute the membrane potential (eq 20)
			self.Em,L = self.newton_raphson(self.compute_all_fluxes,self.Em,verbose = False)
			# self.compute_membrane_potential()
			
			# add up the fluxes
			self.totalionfluxes()

			self.water.compute_flux(self.fHb,self.cbenz2,self.buffer_conc,self.edgto,self.I_18)

			self.integrationInterval()
			self.compute_deltas()

			self.updatecontents()


			

			self.cell.Cat.concentration = self.cell.Cat.amount/self.Vw
			self.cbenz2 = self.benz2/self.Vw


			self.chbetc()



			# Anion and proton ratios
			self.rA = self.medium.A.concentration/self.cell.A.concentration
			self.rH = self.cell.H.concentration/self.medium.H.concentration



			if self.Vw > self.vlysis:
				print "The cells have been lysed and the program has been terminated"
				exit()

			
			# Reversal of Cl x gluconate
			
			if self.I_73 > 0 and self.T_6 > 0:
				if self.sampling_time*60 > self.T_6:
					self.medium.Gluconate.concentration = self.medium.Gluconate.concentration - self.I_73
					self.medium.A.concentration = self.medium.A.concentration + self.I_73
					self.I_73 = 0.0
			




			if self.n_its == 2:
				self.Z = self.Z + 1
				print "Data Sampling number = ",self.Z
				self.publish_all()
				if self.debug:
					self.debugoutput()

			if self.cycle_count == self.cycles_per_print:
				self.Z = self.Z + 1
				print "Publishing at T = {:f} minutes".format(self.sampling_time*60)
				self.publish_all()
				# if options.get('debug',0) != 0:
				if self.debug:
					self.debugoutput()
				self.cycle_count = 0

			if self.sampling_time * 60 > self.duration_experiment:
				print "Experiment over!"
				mainloopfinished = True



		# publish the final value
		self.publish_all()
		self.finished = True


		# Set the time flag so that the user has to change the time in order to run any further
		self.time_set = False
		self.in_progress = False
		try:
			self.write_csv(self.observers[0],"debug.txt")
			self.write_json(self.observers[0],"debug.json")
		except IOError as e:
			print "Unable to write debug file, permission denied"
			print e

		self.stage += 1



		# 'Mg subroutines

		# mgnr:
		# x3=0.02
		# d=0.0001

		# nrmg:
		# x1=x3-d:x6=x1
		# mgf=x6
		# GOSUB eqmg
		# y1=y
		# x2=x3+d:x6=x2
		# mgf=x6
		# GOSUB eqmg
		# y2=y
		# s=(y2-y1)/(x2-x1)
		# x3=x1-(y1/s)
		# x6=x3
		# mgf=x6
		# GOSUB eqmg
		# if abs(y)<0.00001 then return else goto nrmg

		# eqmg:
		# mgb=mgb0+((atp/v(2))*mgf/(0.08+mgf))+((dpgp/v(2))*mgf/(3.6+mgf))
		# y=q(7)-mgf*(v(1)/(v(1)+a(9)/136))-mgb
		# return

		# 'Ca subroutines

		# canr:
		# caf=caf*1000
		# cat=cat*1000
		# qca=cat
		# b1ca=b1ca*1000
		# b1cak=b1cak*1000
		# benz2=benz2*1000
		# benz2k=benz2k*1000

		# x3=caf
		# d=0.000001

		# nrca:
		# x1=x3-d:x6=x1
		# GOSUB eqca
		# y1=y
		# x2=x3+d:x6=x2
		# GOSUB eqca
		# y2=y
		# s=(y2-y1)/(x2-x1)
		# x3=x1-(y1/s)
		# x6=x3
		# GOSUB eqca
		# if abs(y)<0.000001 then goto micromilli else goto nrca

		# micromilli:
		# caf=x3
		# caf=caf/1000
		# cat=cat/1000
		# qca=cat
		# b1ca=b1ca/1000
		# b1cak=b1cak/1000
		# benz2=benz2/1000
		# benz2k=benz2k/1000
		# cab=caf*(alpha^(-1)-v(1)/(v(1)+a(9)/136))+((b1ca/v(2))*caf/(b1cak+caf))+(benz2/v(2))*(caf/(benz2k+caf))
		# return

		# eqca:
		# cabb=x6*(alpha^(-1))+((b1ca/v(2))*x6/(b1cak+x6))+((benz2/v(2))*x6/(benz2k+x6))
		# y=cat-cabb
		# return

		# eff:
		# 'set external tracer to zero
		# 'Memorize tracer values at the start of the efflux period to compute efflux.
		# m(11)=0.00001#
		# m(12)=0.00001#
		# m(13)=0.00001#
		# i(86)=q(10)/q(1)
		# i(87)=q(11)/q(2)
		# i(88)=q(12)/q(3)
		# i(90)=q(10)
		# i(91)=q(11)
		# i(92)=q(12)
		# t(11)=t(4)
		# return

		# phadjust:
		# 'called from screen 2 entries after 4350
		# m(4)=10#^(-v(12))
		# 'Protonized buffer concentration
		# if buffer$="hepes" then goto hep else goto otherbuffer

		# hep:
		# pkhepes=7.83-0.014*b(8)
		# a(5)=10^(-pkhepes)
		# M(6)=M(5)*(M(4)/(A(5)+M(4))):goto mediadjust

		# otherbuffer:
		# a(5)=10^(-pka)
		# M(6)=M(5)*(M(4)/(A(5)+M(4)))
		# if buffer$="a" or buffer$="A" then goto mediadjust
		# if buffer$="c" or buffer$="C" then goto mediadjustcationic

		# 'Medium Na,K,or A concentration
		# mediadjust:
		# IF V(12)>=A(12) THEN naoh ELSE hcl

		# 'Medium pH adjusted with NaOH
		# edgneg=2*edg2+3*edg3+4*edg4+2*(edgca+edgmg)
		# naoh:
		# M(1)=M(3)+edgneg+M(7)+(M(5)-m(6))-M(9)-M(2)-2*mgfo-2*cafo:return
		# 'Medium pH adjusted with KOH
		# koh:
		# M(2)=M(3)+edgneg+M(7)+(M(5)-m(6))-M(9)-M(1)-2*mgfo-2*cafo:return
		# 'Medium pH adjusted with HCl
		# hcl:
		# M(3)=M(1)+M(2)+M(9)-(M(5)-m(6))-M(7)-edgneg+2*mgfo+2*cafo
		# return

		# mediadjustcationic:
		# IF V(12)>=A(12) THEN naohc ELSE hclc

		# 'Medium pH adjusted with NaOH
		# edgneg=2*edg2+3*edg3+4*edg4+2*(edgca+edgmg)
		# naohc:
		# M(1)=M(3)+edgneg+M(7)-m(6)-M(9)-M(2)-2*mgfo-2*cafo:return
		# 'Medium pH adjusted with KOH
		# kohc:
		# M(2)=M(3)+edgneg+M(7)-m(6)-M(9)-M(1)-2*mgfo-2*cafo:return
		# 'Medium pH adjusted with HCl
		# hclc:
		# M(3)=M(1)+M(2)+M(9)+m(6)-M(7)-edgneg+2*mgfo+2*cafo
		# return
	

	def totalCaFlux(self):
		# 'Total Ca flux
		self.total_flux_Ca = self.a23.flux_Ca + self.passiveCa.flux + self.capump.flux_Ca

	def totalFlux(self):
		# Sum of current carrying fluxes for zero current
		self.total_flux = self.napump.total_flux + self.goldman.flux_H + self.goldman.flux_Na + self.goldman.flux_K - self.goldman.flux_A + self.capump.cah*self.capump.flux_Ca + 2.0*self.passiveCa.flux


	def totalionfluxes(self):
		# Total ion fluxes
		# Na flux
		self.total_flux_Na = self.napump.flux_net + self.carriermediated.flux_Na + self.goldman.flux_Na + self.cotransport.flux_Na
		# K flux
		self.total_flux_K = self.napump.flux_K + self.carriermediated.flux_K + self.goldman.flux_K + self.cotransport.flux_K
		# Anion flux
		self.total_flux_A = self.goldman.flux_A + self.cotransport.flux_A + self.JS.flux_A + self.carriermediated.flux_Na + self.carriermediated.flux_K
		# Net proton flux, includes H-flux through Ca pump
		self.total_flux_H = self.JS.flux_H + self.goldman.flux_H - 2*self.a23.flux_Mg-2*self.a23.flux_Ca+self.capump.flux_H


	def integrationInterval(self):
		# 8010 Integration interval
		I_23 = 10.0 + 10.0*math.fabs(self.a23.flux_Mg+self.total_flux_Ca) + math.fabs(self.goldman.flux_H) + math.fabs(self.dedgh) + math.fabs(self.total_flux_Na) + math.fabs(self.total_flux_K) + math.fabs(self.total_flux_A) + math.fabs(self.total_flux_H) + math.fabs(self.water.flux*100.0)
		self.delta_time = self.integration_interval_factor/I_23
		self.sampling_time = self.sampling_time + self.delta_time
		self.cycle_count = self.cycle_count + 1.0
		self.n_its = self.n_its + 1.0

	def compute_deltas(self):
		self.delta_Na = self.total_flux_Na*self.delta_time
		self.delta_K = self.total_flux_K*self.delta_time
		self.delta_A = self.total_flux_A*self.delta_time
		self.delta_H = self.total_flux_H*self.delta_time
		self.delta_Water = self.water.flux*self.delta_time
		self.delta_Mg = self.a23.flux_Mg*self.delta_time
		self.delta_Ca = self.total_flux_Ca*self.delta_time

	def chbetc(self):
		self.cell.Hb.concentration = self.cell.Hb.amount/self.Vw
		self.cell.Mgt.concentration = self.cell.Mgt.amount/self.Vw
		self.cell.X.concentration = self.cell.X.amount/self.Vw
		self.cell.XHbm.amount = self.Q_4 + self.A_10*self.cell.X.amount - 2*self.benz2
		self.cell.XHbm.concentration = self.cell.XHbm.amount/self.Vw
		self.cell.COs.concentration = self.fHb*self.cell.Hb.amount/self.Vw
		# Concentration of charge on Hb
		self.cell.Hbpm.concentration = self.nHb*self.cell.Hb.amount/self.Vw

		# This line doesn't seem to ever be used?
		# self.I_12 = self.cell.Na.concentration + self.cell.K.concentration + self.cell.A.concentration + self.fHb*self.cell.Hb.concentration + self.cell.X.concentration + self.mgf + self.caf + self.cbenz2

		# Sum M
		self.medium.Os.concentration = self.medium.Na.concentration+ self.medium.K.concentration + self.medium.A.concentration + self.buffer_conc + self.medium.Gluconate.concentration + self.medium.Glucamine.concentration + self.medium.Sucrose.concentration + (self.medium.Mgf.concentration + self.medium.Caf.concentration + self.edgto)
		self.cell.Os.amount = self.cell.Na.amount + self.cell.K.amount + self.cell.A.amount + self.fHb*self.cell.Hb.amount + self.cell.X.amount + self.cell.Mgt.amount + (self.cell.Mgf.concentration+self.cell.Caf.concentration)*self.Vw + self.benz2


	def eqmg(self,mgf):
		# eqmg:
		self.mgb = self.mgb0 + ((self.atp/self.VV)*mgf/(0.08+mgf)) + ((self.dpgp/self.VV)*mgf/(3.6+mgf))
		y = self.cell.Mgt.amount - mgf*(self.Vw/(self.Vw+self.hb_content/136.0)) - self.mgb
		return y


	def canr(self):
		self.cell.Caf.concentration = self.cell.Caf.concentration*1000.0
		self.cell.Cat.amount = self.cell.Cat.amount*1000.0
		self.b1ca = self.b1ca*1000.0
		self.b1cak= self.b1cak*1000
		self.benz2=self.benz2*1000
		self.benz2k=self.benz2k*1000


		self.cell.Caf.concentration,L = self.newton_raphson(self.eqca,self.cell.Caf.concentration,step = 0.000001,stop = 0.000001)

		# self.cell.Caf.concentration=x3
		self.cell.Caf.concentration=self.cell.Caf.concentration/1000.0
		self.cell.Cat.amount=self.cell.Cat.amount/1000.0
		self.b1ca=self.b1ca/1000.0
		self.b1cak=self.b1cak/1000.0
		self.benz2=self.benz2/1000.0
		self.benz2k=self.benz2k/1000.0
		self.cab = self.cell.Caf.concentration*(self.alpha**(-1) - self.Vw/(self.Vw+self.hb_content/136.0)) + ((self.b1ca/self.VV)*self.cell.Caf.concentration/(self.b1cak + self.cell.Caf.concentration)) + (self.benz2/self.VV)*(self.cell.Caf.concentration/(self.benz2k+self.cell.Caf.concentration))
		# return

	def eqca(self,x6):
		# eqca
		self.cabb = x6*(self.alpha**(-1.0))+((self.b1ca/self.VV)*x6/(self.b1cak+x6))+((self.benz2/self.VV)*x6/(self.benz2k+x6))
		y = self.cell.Cat.amount-self.cabb
		return y

	def newton_raphson(self,function,initial,max_its = 100,step = 0.001,stop = 0.0001,initial_its = 0,verbose = False):
		X_3 = initial
		no_its = initial_its
		finished = False
		while finished == False:
			X_1 = X_3 - step
			if verbose:
				print
				print
			Y_1 = function(X_1)
			if verbose:
				print X_1,Y_1
			X_2 = X_3 + step
			Y_2 = function(X_2)
			if verbose:
				print X_2,Y_2
			S = (Y_2-Y_1)/(X_2-X_1)
			if verbose:
				print "S: {}, Y/S: {}".format(S,Y_1/S)
			X_3 = X_1 - (Y_1/S)
			# The following tests mean the the code output is identical
			# to Arieh's. However, I think they could be removed for clarity
			# and the model will be essentially identical
			if function != self.ligeq3 and function != self.ligeq1:
				Y_3 = function(X_3)
			else:
				Y_3 = Y_2
			if verbose:
				print Y_3
			no_its += 1
			if no_its > max_its:
				finished = True
			if math.fabs(Y_3) < stop:
				finished = True

		return X_3,no_its


	# def compute_membrane_potential(self):
	# 	# Finds the membrane potential (Em) that gives a total flux of 0
	# 	X_3 = self.Em
	# 	N = 0
	# 	L = 0
	# 	finished = False
	# 	while finished == False:
	# 		X_1 = X_3 - self.D_6
	# 		new_Em = X_1
	# 		if N == 0:
	# 			self.compute_all_fluxes(new_Em)
	# 		Y_1 = self.total_flux
	# 		X_2 = X_3 + self.D_6
	# 		new_Em = X_2
	# 		if N == 0:
	# 			self.compute_all_fluxes(new_Em)
	# 		Y_2 = self.total_flux
	# 		S = (Y_2-Y_1)/(X_2-X_1)
	# 		X_3 = X_1-(Y_1/S)
	# 		new_Em = X_3
	# 		if N == 0:
	# 			self.compute_all_fluxes(new_Em)
	# 		Y_3 = self.total_flux
	# 		L = L + 1
	# 		if math.fabs(Y_3)<self.D_12:
	# 			finished = True
	# 	self.Em = X_3

	def compute_all_fluxes(self,Em):
		self.goldman.compute_flux(Em,self.temp_celsius,self.I_18)
		self.a23.compute_flux(self.I_18)
		self.passiveCa.compute_flux(self.I_18)
		self.capump.compute_flux()
		self.totalCaFlux()
		self.totalFlux()
		return self.total_flux

	def updatecontents(self):
		Vw_old = self.Vw
		self.Vw = self.Vw + self.delta_Water
		self.cell.Hb.concentration = self.cell.Hb.amount/self.Vw
		self.fHb = 1.0 + self.A_2*self.cell.Hb.concentration+self.A_3*self.cell.Hb.concentration**2
		I_28 = self.fHb*self.cell.Hb.amount
		self.cell.Na.amount = self.cell.Na.amount + self.delta_Na
		self.cell.K.amount = self.cell.K.amount + self.delta_K
		self.cell.A.amount = self.cell.A.amount + self.delta_A
		self.Q_4 = self.Q_4 + self.delta_H

		self.nHb = self.Q_4/self.cell.Hb.amount
		self.cell.Mgt.amount = self.cell.Mgt.amount+self.delta_Mg
		self.cell.Cat.amount = self.cell.Cat.amount + self.delta_Ca
		self.gca = self.cell.Cat.amount

		# Cell pH and cell proton concentration
		self.cell.pH = self.I_74 + self.nHb/self.A_1
		self.cell.H.concentration = 10**(-self.cell.pH)
		self.nHb = self.A_1*(self.cell.pH-self.I_74)
		self.Q_8 = I_28
		self.VV = (1-self.A_11) + self.Vw
		self.mchc = self.hb_content/self.VV
		self.density = (self.hb_content/100 + self.Vw)/self.VV
		self.fraction = self.A_7*self.VV

		# External concentrations
		I_30 = 1 + (self.Vw-Vw_old)*self.A_8
		self.medium.Na.concentration = self.medium.Na.concentration*I_30 - self.delta_Na*self.A_8
		self.medium.K.concentration = self.medium.K.concentration*I_30 - self.delta_K*self.A_8
		self.medium.A.concentration = self.medium.A.concentration*I_30 - self.delta_A*self.A_8
		self.medium.Gluconate.concentration = self.medium.Gluconate.concentration*I_30
		self.medium.Glucamine.concentration = self.medium.Glucamine.concentration*I_30
		self.medium.Sucrose.concentration = self.medium.Sucrose.concentration*I_30
		self.buffer_conc = self.buffer_conc*I_30
		self.medium.Hb.concentration = self.medium.Hb.concentration*I_30

		# Medium proton, Ca2+, Mg2+, free and bound buffer and ligand concentrations
		self.medium.Mgt.concentration = self.medium.Mgt.concentration*I_30 - self.delta_Mg*self.A_8
		self.medium.Cat.concentration = self.medium.Cat.concentration*I_30 - self.delta_Ca*self.A_8
		self.edgto = self.edgto*I_30
		if self.edgto == 0:
			self.medium.Mgf.concentration = self.medium.Mgt.concentration
			self.medium.Caf.concentration = self.medium.Cat.concentration
		
		if self.ligchoice != 0:
			self.edgta()
		else:
			self.medium.Hb.concentration = self.medium.Hb.concentration*I_30 - self.delta_H*self.A_8
			self.medium.H.concentration = self.A_5*(self.medium.Hb.concentration/(self.buffer_conc-self.medium.Hb.concentration))
			self.medium.pH = -math.log(self.medium.H.concentration)/math.log(10.0)


		# Cell concentrations and external concentrations
		self.cell.Na.concentration = self.cell.Na.amount/self.Vw
		self.cell.K.concentration = self.cell.K.amount/self.Vw
		self.cell.A.concentration = self.cell.A.amount/self.Vw

		# compute mgf
		self.cell.Mgf.concentration,L = self.newton_raphson(self.eqmg,0.02,step = 0.0001,stop = 0.00001)
		# compute caf
		self.canr()


	def set_temp_permeability_options(self,options,used_options = []):

		print
		print
		print "********************************"
		print "Temperature Permeability Options"
		print "********************************"


		# 4950 CLS:'......................................Screen 4.................
		# 4970 PRINT "4. TEMPERATURE (oC) AND PERMEABILITIES (1/h).": PRINT
		# 4990 PRINT
		# 5010 PRINT "VARIABLE";TAB(32);"DEFAULT VALUE";TAB(52);"NEW VALUE or
		# 'Enter'"
		# 5030 PRINT: PRINT "Temperature:  ";
		# 5050 PRINT TAB(36); USING "##.# ";B(8);TAB(55);
		# 5070 I(42)=B(8)
		defaultTemp = self.temp_celsius
		if 'temperature' in options:
			self.temp_celsius = options.get('temperature')
			print "Temperature set to {:g}".format(self.temp_celsius)
			used_options.append('temperature')
			# 'Temperature-dependence of the pI of haemoglobin
			# piold=a(4)-(0.016*i(42))
			# pinew=a(4)-(0.016*b(8))
			# i(74)=pinew
			piold = self.pit0 - (0.016*defaultTemp)
			pinew = self.pit0 - (0.016*self.temp_celsius)
			self.I_74 = pinew
			# newphc=pinew-piold+v(11)
			# v(11)=newphc
			# c(4)=10#^(-v(11))
			newphc = pinew - piold + self.cell.pH
			self.cell.pH = newphc
			self.cell.H.concentration = 10**(-self.cell.pH)
			
			if self.BufferType == 'HEPES':
				self.pkhepes = 7.83 - 0.014*self.temp_celsius
				a5old = self.A_5
				m4old = self.medium.H.concentration
				self.A_5 = 10.0**(-self.pkhepes)
				self.medium.H.concentration = self.A_5*m4old/a5old
				self.medium.pH = -math.log(self.medium.H.concentration)/math.log(10.0)
				self.medium.Hb.concentration = self.buffer_conc*(self.medium.H.concentration/(self.A_5+self.medium.H.concentration))
			else:
				# ac:
				# a(5)=10^(-pka)
				# m(4)=a(5)*m4old/a5old
				# v(12)=-(LOG(m(4))/LOG(10#))
				# m(6)=m(5)*(m(4)/(a(5)+m(4)))
				# '+++++++++++++++++++++++++++++++++++++
				# SR - weird here - check with Arieh...
				# m4old = a5old = 0?
				self.A_5 = 10.0**(-self.pka)
				self.medium.H.concentration = self.A_5*m4old/a5old
				self.medium.pH = -math.log(self.medium.H.concentration)/math.log(10.0)
				self.medium.Hb.concentration = self.buffer_conc*(self.medium.H.concentration/(self.A_5+self.medium.H.concentration))

			
		else:
			"Temperature set to default ({:g})".format(defaultTemp)
		# 5090 INPUT "(not 0) ", B(8)
		# 5110 IF B(8)=0 THEN B(8)=I(42)

		# if b(8)=i(42) then goto 5130

		

		# 5130 PRINT "Water permeability:  ";
		# 5150 PRINT TAB(36); USING "##.##^^^^ ";P(10);TAB(55);
		# 5170 I(38)=P(10)
		# 5190 INPUT P(10)
		# 5210 IF P(10)=0 THEN P(10)=I(38)
		# 5230 PRINT
		if "water-perm" in options:
			self.water.permeability = options.get("water-perm")
			print "Water Permeability set to {:g}".format(self.water.permeability)
			used_options.append('water-perm')
		else:
			print "Water permeability set to default ({:g})".format(self.water.permeability)

		# 5250 PRINT "Electrodiffusional permeabilities:  "
		# 5270 PRINT "PGK";
		# 5290 PRINT TAB(36); USING "##.###### ";P6;TAB(55)
		# 5310 I(35)=P6
		# 5330 INPUT P6
		# 5350 IF P6=0 THEN P6=I(35)
		
		if "pgk" in options:
			self.goldman.permeability_K = options.get("pgk")
			used_options.append('pgk')
			print "PGK set to {:g}".format(self.goldman.permeability_K)
		else:
			print "PGK set to default ({:g})".format(self.goldman.permeability_K)

		# P6 = 30


		# 5355 PRINT "PGKH";
		# 5360 PRINT TAB(36); USING "##.###### ";I(63);TAB(55)
		# 5364 INPUT I(63)
		if "pgkh" in options:
			self.goldman.pgkh = options.get("pgkh")
			used_options.append(pgkh)
			print "PGKH set to {:g}".format(self.goldman.pgkh)
		else:
			print "PGKH set to default ({:g})".format(self.goldman.pgkh)


		# 5370 PRINT "PGNa";
		# 5390 PRINT TAB(36); USING "##.###### ";P(4);TAB(55)
		# 5410 I(39)=P(4)
		# 5430 INPUT P(4)
		# 5450 IF P(4)=0 THEN P(4)=I(39)
		if "pgna" in options:
			self.goldman.permeability_Na = options.get("pgna")
			used_options.append('pgna')
			print "PGNa set to {:g}".format(self.goldman.permeability_Na)
		else:
			print "PGNa set to default ({:g})".format(self.goldman.permeability_Na)


		# 5470 PRINT "PGA";
		# 5490 PRINT TAB(36); USING "##.###### ";P(7);TAB(55)
		# 5510 I(36)=P(7)
		# 5530 INPUT P(7)
		# 5550 IF P(7)=0 THEN P(7)=I(36)
		if "pga" in options:
			self.goldman.permeability_A = options.get("pga")
			used_options.append('pga')
			print "PGA set to {:g}".format(self.goldman.permeability_A)
		else:
			print "PGA set to default ({:g})".format(self.goldman.permeability_A)

		


		# PRINT "PGH";
		# PRINT TAB(36); USING "##.##^^^^ ";e(8);TAB(55)
		# pghdefault=e(8)
		# INPUT e(8)
		# IF e(8)=0 THEN e(8)=pghdefault
		if "pgh" in options:
			self.goldman.permeability_H = options.get("pgh")
			used_options.append('pgh')
			print "PGH set to {:g}".format(self.goldman.permeability_H)
		else:
			print "PGH set to default ({:g})".format(self.goldman.permeability_H)


		# PRINT "PMg/Ca(A23187) (high=2e18)";
		# PRINT TAB(36); USING "##.##^^^^ ";pmg;TAB(55)
		# I(56)=pmg
		# INPUT pmg
		# IF pmg=0 THEN pmg=I(56)
		# pca=pmg
		if "pmg" in options:
			self.a23.permeability_Mg = options.get("pmg")
			used_options.append('pmg')
			print "PMg set to {:g}".format(self.a23.permeability_Mg)
			if 'a23cam' in options:
				self.a23.camk = options.get("a23cam")
				used_options.append('a23cam')
			else:
				self.a23.camk = 10.0

			if 'a23mgm' in options:
				self.a23.mgmk = options.get('a23mgm')
				used_options.append('a23mgm')
			else:
				self.a23.mgmk = 10.0


			if 'a23cai' in options:
				self.a23.caik = options.get('a23cai')
				used_options.append('a23cai')
			else:
				self.a23.caik = 10.0

			if 'a23mgi' in options:
				self.a23.mgik = options.get('a23mgi')
				used_options.append('a23mgi')
			else:
				self.a23.mgik = 10.0


			print "a23camk = {0:g}, a23mgmk = {1:g}, a23caik = {2:g}, a23mgik = {3:g}".format(
				self.a23.camk,self.a23.mgmk,self.a23.caik,self.a23.mgik)
		else:
			print "PMg set to default ({:g})".format(self.a23.permeability_Mg)

		self.a23.permeability_Ca = self.a23.permeability_Mg

		if "pit0" in options:
			self.pit0 = options.get("pit0")
			used_options.append('pit0')
			print "pI(T=0) set to {:g}".format(self.pit0)
			self.cell.pH = self.pit0 - self.I_67 + self.cell.pH
			self.cell.H.concentration = 10**(-self.cell.pH)
			self.I_74 = self.pit0 - (0.016*self.temp_celsius)
			if options.get("deoxy","N") == "Y":
				self.atp = self.atp/2.0
				self.dpgp = self.dpgp/1.7
				print "Deoxy Induced Mg2+i increase turned ON"
			else:
				print "Deoxy Induced Mg2+i increase turned OFF"
		else:
			print "pI(T=0) set to default ({:g})".format(self.pit0)
		

	def set_transport_changes_options(self,options,used_options = []):
		print 
		print
		print "*************************"
		print "Transport Changes Options"
		print "*************************"
		# 3430 CLS:'.....................................Screen 3....................

		# If flagnapscreen3<>1 then goto 3450:'return to the screen of screens

		# print
		# print "DO NOT RETURN TO THIS SCREEN; IN ERROR, PRESS ^BREAK AND START AGAIN"
		# PRINT "_____________________________________________________________________"
		# print "3. TRANSPORT CHANGES
		# PRINT
		# print "The original forward Vmax of the Na-pump was 11.4 mmol/loch. The"
		# print "current value is";
		# print using "##.##^^^^ ";p(1);:print "."
		# input "ENTER to restore the original value or change to:     ",napvmax
		# if napvmax=0 then p(1)=11.4
		if 'na-pump-flux-change' in options:
			self.napump.P_1 = options.get('na-pump-flux-change')
			used_options.append('na-pump-flux-change')

		# print:print
		# print "The original reverse Vmax of the Na-pump was 11.8 mmol/loch. The"
		# print "current value is";
		# print using "##.##^^^^ ";p(2);:print "."
		# input "ENTER to restore the original value or change to:     ",rnapvmax
		# if rnapvmax=0 then p(2)=11.8
		if 'na-pump-reverse-flux-change' in options:
			self.napump.P_2 = options.get('na-pump-reverse-flux-change')
			used_options.append('na-pump-reverse-flux-change')

		# print
		# print "The original Na leak (Na:A) rate was 1.15e-4 (1/h). The current value"
		# print "is";
		# print using "##.##^^^^ ";p(3);
		# input ". ENTER to restore the original value or change to:     ",naleak
		# if naleak=0 then p(3)=1.15e-4
		if 'naa-change' in options:
			self.carriermediated.permeability_Na *= options.get('naa-change')
			used_options.append('naa-change')

		# print
		# print "The original K leak (K:A) rate was 1.25e-4 (1/h). The current value"
		# print "is";
		# print using "##.##^^^^ ";p(5);
		# input ". ENTER to restore the original value or change to:     ",potleak
		# if potleak=0 then p(5)=1.25e-4
		if 'ka-change' in options:
			self.carriermediated.permeability_K *= options.get('ka-change')
			used_options.append('ka-change')

		# print
		# PRINT "Na:K:2Cl-cotransport, off (ENTER) or type % activation"
		# INPUT "of maximally allowed activity (about 1mM/loch):   ";I(59)
		# P(8)=.0002*(I(59)/100)
		# PRINT
		# The following gives slightly different results to Ariehs.
		# To reproduce Arieh's, set cotransport_factor to zero
		if 'cotransport-activation' in options:
			cotransport_factor = options.get('cotransport-activation')
			self.cotransport.permeability = 0.0002 * cotransport_factor / 100.0
			used_options.append('cotransport-activation')

		# print "J-S cycle normal (ENTER) or enter stimulation/inhibition"
		# input "factor:       ";I(47)
		# if i(47)=0 then goto 3730
		# P(9)=P(9)*I(47)
		if 'js-stimulation-inhibition' in options:
			jsfactor = options.get('js-stimulation-inhibition')
			self.JS.permeability *= jsfactor
			print "js-stimulation-inhibition set to {}".format(jsfactor)
			used_options.append('js-stimulation-inhibition')


		# print
		# print "Vmax of Ca pump: default (ENTER) or enter stimulation/inhibition"
		# input "factor:      ";I(84)
		# if i(84)<>0 then fcapm=fcapm*i(84)
		if 'vmax-pump-change' in options:
			self.capump.fcapm *= options.get('vmax-pump-change')
			used_options.append('vmax-pump-change')

		# PRINT
		# print "Vmax of Ca leak: default (ENTER) or enter stimulation/inhibition"
		# input "PCaG-factor:      ";I(85)
		# 'if i(85)=0 then goto 3850
		# if i(85)<>0 then self.passiveCa.fcalm=self.passiveCa.fcalm*i(85)
		if 'vmax-leak-change' in options:
			self.passiveCa.fcalm *= options.get('vmax-leak-change')
			used_options.append('vmax-leak-change')

		# print
		# input "Enter % inhibition of Gardos channel (ENTER for default=0):  "; gardosinhibition
		# pkm=pkm*(100-gardosinhibition)/100
		if 'percentage-inhibition' in options:
			self.goldman.pkm *= (100.0 - options.get('percentage-inhibition'))/100.0
			used_options.append('percentage-inhibition')


		# goto 2370

		# 3450 PRINT
		# 3470 PRINT "3. TRANSPORT CHANGES"
		# 3490 PRINT
		# 3510 PRINT "If you make a mistake here you must press Ctrl-Break and start again."
		# 3530 PRINT "You cannot return to this screen to correct errors.":PRINT

		# 3550 INPUT "Na-pump normal (ENTER) or type % inhibition (0-100):";I(51)
		# 3570 P(1)=P(1)*(1-(I(51)/100)):P(2)=P(2)*(1-(I(51)/100))
		# 3590 PRINT

		# 3610 PRINT "Na:K:2Cl-cotransport, off (ENTER) or type % activation"
		# 3630 INPUT "of maximally allowed activity (about 1mM/loch):   ";I(59)
		# 3650 P(8)=.0002*(I(59)/100)
		# 3670 PRINT


		# print "J-S cycle normal (ENTER) or enter stimulation/inhibition"
		# input "factor:       ";I(47)
		# if i(47)=0 then goto 3730
		# P(9)=P(9)*I(47)

		# 3730 PRINT

		# print "PL(Na:A) normal (ENTER) or enter stimulation/inhibition"
		# input "factor:  ";I(60)
		# if i(60)=0 then goto 3750
		# P(3)=P(3)*I(60)
		# 3750 PRINT

		# print "PL(K:A) normal (ENTER) or enter stimulation/inhibition"
		# input "factor:  ";I(61)
		# if i(61)=0 then goto 3752
		# P(5)=P(5)*I(61)

		# 3752 print
		# print "Vmax of Ca pump: default (ENTER) or enter stimulation/inhibition"
		# input "factor:      ";I(84)
		# if i(84)<>0 then fcapm=fcapm*i(84)

		# PRINT
		# print "Vmax of Ca leak: default (ENTER) or enter stimulation/inhibition"
		# input "PCaG-factor:      ";I(85)
		# 'if i(85)=0 then goto 3850
		# if i(85)<>0 then self.passiveCa.fcalm=self.passiveCa.fcalm*i(85)

		# print
		# input "Enter % inhibition of Gardos channel (ENTER for default=0):  "; gardosinhibition
		# pkm=pkm*(100-gardosinhibition)/100

		# goto 2370

		# '3850 PRINT:PRINT
		# '3870 INPUT "Press 'ENTER' to continue - ",Z$
		# '3890 GOTO 2370
		print
		print



	def set_cell_fraction_options(self,options,used_options = []):
		print
		print
		print "********************************"
		print "Cell Fraction and Medium Changes"
		print "********************************"
		if 'fraction' in options:
			self.fraction = options.get('fraction')
			used_options.append('fraction')
			print "Cell fraction set to {:g}".format(self.fraction)
		else:
			print "Cell fraction set to default ({:g})".format(self.fraction)
		if self.A_7 != self.fraction:
			self.A_7 = self.fraction
			self.A_8 = self.A_7/(1-self.A_7)

		if 'buffer-name' in options:
			buffer_choice = int(options.get('buffer-name'))

			# Purge this when no old models exist
			# if not hasattr(self,'buffers'):
			self.buffers = {0:"HEPES",1:"A",2:"C"}

			print self.buffers
			self.BufferType = self.buffers[buffer_choice]

			used_options.append('buffer-name')
			print "Buffer type set to {:s}".format(self.BufferType)
			self.pka = options.get('pka',self.pka)
		else:
			print "Buffer Type set to default ({:s})".format(self.BufferType)

		if 'bufferconc' in options:
			self.buffer_conc = options.get('bufferconc')
			used_options.append('bufferconc')
			print "Buffer Concentration set to {:f}".format(self.buffer_conc)
		else:
			print "Buffer Concentration set to default ({:f})".format(self.buffer_conc)

		self.A_12 = self.medium.pH
		if "extph" in options:
			self.medium.pH = options.get('extph')
			used_options.append('extph')
			print "Initial external pH set to {:f}".format(self.medium.pH)
		else:
			print "Initial external pH set to default ({:f})".format(self.medium.pH)

		self.phadjust()



		if 'nag' in options:
			self.I_72 = options.get("nag")
			used_options.append('nag')
			self.medium.Glucamine.concentration = self.medium.Glucamine.concentration + self.I_72
			self.medium.Na.concentration= self.medium.Na.concentration- self.I_72




		if 'acl' in options:
			self.I_73 = options.get("acl")
			self.medium.Gluconate.concentration = self.medium.Gluconate.concentration + self.I_73
			self.medium.A.concentration = self.medium.A.concentration - self.I_73
			if self.I_73 != 0:
				if 'clxdur' in options:
					self.T_6 = options.get("clxdur")
					used_options.append('clxdur')



		# Note that it is important that we don't run these bits twice!
		if 'na-for-kcl' in options:
			I_40 = options.get('na-for-kcl')
			self.medium.Na.concentration= self.medium.Na.concentration- I_40
			self.medium.K.concentration = self.medium.K.concentration + I_40
			used_options.append('na-for-kcl')
		if 'kcl_for_na' in options:
			I_33 = options.get('kcl-for-na')
			self.medium.Na.concentration= self.medium.Na.concentration+ I_33
			self.medium.K.concentration = self.medium.K.concentration - I_33
			used_options.append('kcl-for-na')
		if 'change-nacl' in options:
			I_45 = options.get('change-nacl')
			self.medium.Na.concentration= self.medium.Na.concentration+ I_45
			self.medium.A.concentration = self.medium.A.concentration + I_45
			used_options.append('change-nacl')
		if 'change-kcl' in options:
			I_34 = options.get('change-kcl')
			self.medium.K.concentration = self.medium.K.concentration + I_34
			self.medium.A.concentration = self.medium.A.concentration + I_34
			used_options.append('change-kcl')
		if 'add-sucrose' in options:
			I_46 = options.get('add-sucrose')
			self.medium.Sucrose.concentration = self.medium.Sucrose.concentration + I_46
			used_options.append('add-sucrose')

		if 'mgot' in options:
			mgtold = self.medium.Mgt.concentration
			self.medium.Mgt.concentration = options.get('mgot',self.medium.Mgt.concentration)
			used_options.append('mgot')
			if self.medium.Mgt.concentration != 0:
				self.medium.A.concentration = self.medium.A.concentration + 2*(self.medium.Mgt.concentration-mgtold)
			print "MgoT set to {:f}".format(self.medium.Mgt.concentration)
		else:
			print "MgoT set to default ({:f})".format(self.medium.Mgt.concentration)




		if 'caot' in options:
			catold = self.medium.Cat.concentration
			self.medium.Cat.concentration = options.get('caot',self.medium.Cat.concentration)
			used_options.append('caot')
			if self.medium.Cat.concentration != 0:
				self.medium.A.concentration = self.medium.A.concentration + 2*(self.medium.Cat.concentration - catold)
			print "CaoT set to {:f}".format(self.medium.Cat.concentration)
		else:
			print "Caot set to default ({:f})".format(self.medium.Cat.concentration)



		if 'chelator' in options:
			used_options.append('chelator')
		if 'edgto' in options:
			used_options.append('edgto')
		self.ligchoice = options.get('chelator',self.ligchoice)
		# Note the underscore in the following!!!
		if self.ligchoice == 1:
			# EGTA
			# input "EGTA concentration (in mM)?     ";edgto
			self.edgto = options.get('edgto',self.edgto)
			self.edghk1 = 10**(-9.22)
			self.edghk2 = 10**(-8.65)
			self.edgcak = 10**(-10.34)
			self.edgmgk = 10**(-5.10)
			if self.medium.Cat.concentration<self.edgto:
				self.medium.Caf.concentration = self.medium.Caf.concentration/100000.0
			elif self.medium.Cat.concentration == self.edgto:
				self.medium.Caf.concentration = self.medium.Cat.concentration/100.0
			elif self.medium.Cat.concentration>self.edgto:
				self.medium.Caf.concentration = math.fabs(self.medium.Cat.concentration-self.edgto)
			if self.medium.Mgt.concentration < (self.edgto - self.medium.Cat.concentration):
				self.medium.Mgf.concentration = self.medium.Mgt.concentration/5.0
			else:
				self.medium.Mgf.concentration = self.medium.Mgt.concentration
		elif self.ligchoice == 2:
			# EDTA
			self.edgto = options.get('edgto',self.edgto)
			self.edghk1=10**(-9.84)
			self.edghk2=10**(-5.92)
			self.edgcak=10**(-9.95)
			self.edgmgk=10**(-8.46)

			# Initial cafo/mgfo values for iterative solution
			camgratio=self.medium.Cat.concentration/(self.medium.Cat.concentration+self.medium.Mgt.concentration)
			if self.edgto < (self.medium.Cat.concentration + self.medium.Mgt.concentration):
				self.medium.Caf.concentration = self.medium.Cat.concentration - self.edgto*camgratio
				self.medium.Mgf.concentration = self.medium.Mgt.concentration - self.edgto*(1.0-camgratio)
			elif self.edgto > (self.medium.Cat.concentration + self.medium.Mgt.concentration):
				self.medium.Caf.concentration = self.medium.Cat.concentration/100000.0
				self.medium.Mgf.concentration = self.medium.Mgt.concentration/1000.0
			else:
				self.medium.Caf.concentration = self.medium.Cat.concentration/1000.0
				self.medium.Mgf.concentration = self.medium.Mgt.concentration/10.0

			print "****************************************************************"
			print "THE PROGRAMME MAY FAIL DUE TO EXCESIVE MEDIUM ACIDIFICATION "
			print "FOLLOWING CHELATION. IF SO RUN AGAIN INCREASING pHo IN SCREEN 2."
			print "****************************************************************"
		elif self.ligchoice == 0:
			self.edgto = 0
			self.medium.Mgf.concentration = self.medium.Mgt.concentration
			self.medium.Caf.concentration = self.medium.Cat.concentration
			return

		self.chelator()
		self.oldedgta()
		

	def set_screen_time_factor_options(self,options,used_options = []):
		print
		print
		print "********************"
		print "Setting Time Options"
		print "********************"
		# 2850 '.................Experimental changes begin here......................
		# 2870 CLS
		# 2890 PRINT "1. TIME FACTORS.":PRINT
		# 2910 IF (T(1)*60)=0 THEN GOTO 2930 ELSE GOTO 2990
		# 2930 PRINT "Type a new value then 'Enter' (or 'Enter' alone for default values)."
		# 2950 PRINT
		# 2970 INPUT "LENGTH OF EXPERIMENT in minutes:      ",T(4):PRINT
		# SDR - following are hard-coded
		print "Setting time options"
		if 'time' in options:
			self.duration_experiment = options.get("time")
			used_options.append('time')
			print "Total Length set to {:g}".format(self.duration_experiment)
		# else:
		# 	print "YOU HAVE TO SET AN EXPERIMENT LENGTH"
		# 	exit()
		self.I_43 = self.integration_interval_factor
		if 'integrationfactor' in options:
			self.integration_interval_factor = options.get('integrationfactor')
			used_options.append('integrationfactor')
			print "Integration factor set to {:g}".format(self.integration_interval_factor)
		else:
			self.integration_interval_factor = self.I_43
			print "Integration factor set to default ({:g})".format(self.integration_interval_factor)
		if 'cyclesperprint' in options:
			self.cycles_per_print = options.get('cyclesperprint',777)
			used_options.append('cyclesperprint')
			print "No. of Integration Cycles per printout set to {:g}".format(self.cycles_per_print)
		else:
			print "No. of Integration Cycles per printout set to default ({:g})".format(self.cycles_per_print)
		print
		print

	def phadjust(self):
		# 	phadjust:
		# called from screen 2 entries after 4350
		self.medium.H.concentration = 10**(-self.medium.pH)
		# Protonized buffer concentration
		if self.BufferType == 'HEPES':
			self.pkhepes = 7.83 - 0.014*self.temp_celsius
			self.A_5 = 10**(-self.pkhepes)
			self.medium.Hb.concentration = self.buffer_conc*(self.medium.H.concentration/(self.A_5+self.medium.H.concentration))
		else:
			self.A_5 = 10**(-self.pka)
			self.medium.Hb.concentration = self.buffer_conc*(self.medium.H.concentration/(self.A_5+self.medium.H.concentration))

		if self.BufferType == 'C':
			if self.medium.pH >= self.A_12:
				self.medium.Na.concentration = self.medium.A.concentration+self.edgneg+self.medium.Gluconate.concentration-self.medium.Hb.concentration-self.medium.Glucamine.concentration-self.medium.K.concentration-2*self.medium.Mgf.concentration-2*self.medium.Caf.concentration
				return
			else:
				self.medium.A.concentration = self.medium.Na.concentration + self.medium.K.concentration+self.medium.Glucamine.concentration+self.medium.Hb.concentration-self.medium.Gluconate.concentration-self.edgneg+2*self.medium.Mgf.concentration+2*self.medium.Caf.concentration
				return
		else:
			if self.medium.pH >= self.A_12:
				self.medium.Na.concentration=self.medium.A.concentration+self.edgneg+self.medium.Gluconate.concentration+(self.buffer_conc-self.medium.Hb.concentration)-self.medium.Glucamine.concentration-self.medium.K.concentration-2*self.medium.Mgf.concentration-2*self.medium.Caf.concentration
				return
			else:
				self.medium.A.concentration=self.medium.Na.concentration + self.medium.K.concentration+self.medium.Glucamine.concentration-(self.buffer_conc-self.medium.Hb.concentration)-self.medium.Gluconate.concentration-self.edgneg+2*self.medium.Mgf.concentration+2*self.medium.Caf.concentration
				return


	def chelator(self):
		self.medium.Na.concentration = self.medium.Na.concentration + 2*self.edgto

	def oldedgta(self):
		# The following computes initial "dedgh"=proton release to medium on chelation
		fh = 1 + self.medium.H.concentration/self.edghk1 + (self.medium.H.concentration**2)/(self.edghk1*self.edghk2)
		edg4old = self.edgto/(1000*fh)
		edg3old = edg4old*self.medium.H.concentration/self.edghk1
		edg2old = edg4old*(self.medium.H.concentration**2)/(self.edghk1*self.edghk2)
		self.edghold = edg3old+2*edg2old
		self.edghold = 1000*self.edghold
		self.edgtainitial()
		self.edgta()

		self.edgneg=2*self.edg2+3*self.edg3+4*self.edg4+2*(self.edgca+self.edgmg)

		if self.BufferType == "c" or self.BufferType=="C":
			self.medium.A.concentration = self.medium.Na.concentration+ self.medium.Hb.concentration + self.medium.Glucamine.concentration + 2*self.medium.Mgf.concentration + 2*self.medium.Caf.concentration - self.edgneg - self.medium.Gluconate.concentration
		else:
			self.medium.Na.concentration= self.medium.A.concentration + self.edgneg + self.medium.Gluconate.concentration + (self.buffer_conc - self.medium.Hb.concentration) - self.medium.Glucamine.concentration - self.medium.K.concentration - 2*self.medium.Mgf.concentration - 2*self.medium.Caf.concentration

	def edgta(self):
		self.edghold=self.edg3+2*self.edg2
		self.edgtainitial()


	def edgtainitial(self):
		# Convert ligand, Ca and Mg concentrations to Molar units for ligroots sub
		self.edgto=self.edgto/1000.0
		self.medium.Caf.concentration=self.medium.Caf.concentration/1000.0
		self.medium.Mgf.concentration=self.medium.Mgf.concentration/1000.0
		self.medium.Cat.concentration=self.medium.Cat.concentration/1000.0
		self.medium.Mgt.concentration=self.medium.Mgt.concentration/1000.0
		self.buffer_conc=self.buffer_conc/1000.0
		self.delta_H=self.delta_H/1000.0
		self.dedgh=self.dedgh/1000.0

		fff=1+self.medium.H.concentration/self.edghk1+(self.medium.H.concentration**2)/(self.edghk1*self.edghk2)+self.medium.Caf.concentration/self.edgcak+self.medium.Mgf.concentration/self.edgmgk
		# This parameter was made a class parameter to avoid 
		# extra variables being passed to newton_raphson. 
		# It's only used by ligeq1
		self.lig_hb=self.medium.H.concentration*(self.buffer_conc/(self.medium.H.concentration+self.A_5)+self.edgto/(fff*self.edghk1)+self.medium.H.concentration*self.edgto/(fff*self.edghk1*self.edghk2))

		nn = 0
		finished = False
		while finished == False:
			bbb = 0

			rr = 1
			buff = self.medium.H.concentration
			hhold=buff
			diff1=0.0001*self.medium.H.concentration
			X_3,L = self.newton_raphson(self.ligeq1,buff,step=diff1,stop=self.diff2,max_its=100,initial_its = bbb)
			bbb += L
			if X_3<0:
				X_3 = hhold
			self.medium.H.concentration = X_3

			rr = 2
			buff = self.medium.Caf.concentration
			cafold = buff
			diff1=0.0001*self.medium.Caf.concentration
			X_3,L = self.newton_raphson(self.ligeq2,buff,step=diff1,stop=self.diff2,max_its=100,initial_its = bbb)
			bbb += L
			if X_3<0:
				X_3 = cafold/2.0
			self.medium.Caf.concentration = X_3

			rr = 3
			buff = self.medium.Mgf.concentration
			mgfold = buff
			diff1 = 0.0001*self.medium.Mgf.concentration
			X_3,L = self.newton_raphson(self.ligeq3,buff,step=diff1,stop=self.diff2,max_its=100,initial_its = bbb)
			bbb += L
			if X_3<0:
				X_3=mgfold/2.0
			self.medium.Mgf.concentration=X_3

			nn = nn + 1
			if nn>100:
				finished = True

			if math.fabs(self.medium.H.concentration - hhold)<=self.diff3*hhold and math.fabs(cafold-self.medium.Caf.concentration)<=self.diff3*cafold and math.fabs(mgfold-self.medium.Mgf.concentration)<=self.diff3*mgfold:
				finished = True

		self.medium.pH = -math.log(self.medium.H.concentration)/math.log(10.0)
		self.buffer_conc = self.buffer_conc*1000.0
		self.delta_H = self.delta_H*1000.0
		self.medium.Hb.concentration = self.medium.H.concentration*self.buffer_conc/(self.medium.H.concentration + self.A_5)
		self.edg4 = self.edgto/self.ff
		self.edg3 = self.edg4*self.medium.H.concentration/self.edghk1
		self.edg2 = self.edg4*self.medium.H.concentration**2/(self.edghk1*self.edghk2)
		self.edgca = self.edg4*self.medium.Caf.concentration/self.edgcak
		self.edgmg = self.edg4*self.medium.Mgf.concentration/self.edgmgk
		self.edgneg=2*self.edg2+3*self.edg3+4*self.edg4+2*(self.edgca+self.edgmg)
		self.edghnew=self.edg3+2*self.edg2

		# Convert ligand, Ca and Mg concentrations back to mM units
		self.edgto=self.edgto*1000
		self.medium.Caf.concentration=self.medium.Caf.concentration*1000
		self.medium.Mgf.concentration=self.medium.Mgf.concentration*1000
		self.medium.Cat.concentration=self.medium.Cat.concentration*1000
		self.medium.Mgt.concentration=self.medium.Mgt.concentration*1000
		self.edg4=self.edg4*1000
		self.edg3=self.edg3*1000
		self.edg2=self.edg2*1000
		self.edgca=self.edgca*1000
		self.edgmg=self.edgmg*1000
		self.edgneg=self.edgneg*1000
		self.edghnew=self.edghnew*1000
		# computes d[H]o due to chelation.
		self.dedgh=self.edghnew-self.edghold

	def ligeq1(self,X_3):
		self.ff = 1 + X_3/self.edghk1 + (X_3**2)/(self.edghk1*self.edghk2) + self.medium.Caf.concentration/self.edgcak + self.medium.Mgf.concentration/self.edgmgk
		return X_3*(self.buffer_conc/(X_3 + self.A_5) + self.edgto/(self.ff*self.edghk1) + X_3*self.edgto/(self.ff*self.edghk1*self.edghk2))-(self.lig_hb-self.A_8*self.delta_H-self.dedgh)

	def ligeq2(self,X_3):
		self.ff = 1 + self.medium.H.concentration/self.edghk1 + (self.medium.H.concentration**2)/(self.edghk1*self.edghk2) + X_3/self.edgcak + self.medium.Mgf.concentration/self.edgmgk
		return self.medium.Cat.concentration - X_3*(1+self.edgto/(self.ff*self.edgcak))
		

	def ligeq3(self,X_3):
		self.ff = 1 + self.medium.H.concentration/self.edghk1 + (self.medium.H.concentration**2)/(self.edghk1*self.edghk2) + self.medium.Caf.concentration/self.edgcak + X_3/self.edgmgk
		return self.medium.Mgt.concentration - X_3*(1+self.edgto/(self.ff*self.edgmgk))


	def publish_all(self):
		ti = self.sampling_time*60.0
		self.publish("Time",self.sampling_time*60.0,ti,stage=self.stage)
		self.publish("Vw",self.Vw,ti,stage=self.stage)
		self.publish("V/V",self.VV,ti,stage=self.stage)
		self.publish("MCHC",self.mchc,ti,stage=self.stage)
		self.publish("Density",self.density,ti,stage=self.stage)
		self.publish("pHi",self.cell.pH,ti,stage=self.stage)
		self.publish("pHo",self.medium.pH,ti,stage=self.stage)
		self.publish("Hct",self.fraction*100.0,ti,stage=self.stage)
		self.publish("Em",self.Em,ti,stage=self.stage)
		self.publish("QNa",self.cell.Na.amount,ti,stage=self.stage)
		self.publish("QK",self.cell.K.amount,ti,stage=self.stage)
		self.publish("QA",self.cell.A.amount,ti,stage=self.stage)
		self.publish("QCa",self.cell.Cat.amount,ti,stage=self.stage)
		self.publish("QMg",self.cell.Mgt.amount,ti,stage=self.stage)
		self.publish("CNa",self.cell.Na.concentration,ti,stage=self.stage)
		self.publish("CK",self.cell.K.concentration,ti,stage=self.stage)
		self.publish("CA",self.cell.A.concentration,ti,stage=self.stage)
		self.publish("CCa2+",self.cell.Caf.concentration,ti,stage=self.stage)
		self.publish("CMg2+",self.cell.Mgf.concentration,ti,stage=self.stage)
		self.publish("CHb",self.cell.Hb.concentration,ti,stage=self.stage)
		self.publish("CX",self.cell.X.concentration,ti,stage=self.stage)
		self.publish("COs",self.cell.COs.concentration,ti,stage=self.stage)
		self.publish("rA",self.rA,ti,stage=self.stage)
		self.publish("rH",self.rH,ti,stage=self.stage)
		self.publish("fHb",self.fHb,ti,stage=self.stage)
		self.publish("nHb",self.nHb,ti,stage=self.stage)
		self.publish("MNa",self.medium.Na.concentration,ti,stage=self.stage)
		self.publish("MK",self.medium.K.concentration,ti,stage=self.stage)
		self.publish("MA",self.medium.A.concentration,ti,stage=self.stage)
		self.publish("FNaP",self.napump.flux_net,ti,stage=self.stage)
		self.publish("FCaP",self.capump.flux_Ca,ti,stage=self.stage)
		self.publish("FKP",self.napump.flux_K,ti,stage=self.stage)
		self.publish("FNa",self.total_flux_Na,ti,stage=self.stage)
		self.publish("FK",self.total_flux_K,ti,stage=self.stage)
		self.publish("FA",self.total_flux_A,ti,stage=self.stage)
		self.publish("FH",self.total_flux_H,ti,stage=self.stage)
		self.publish("FW",self.water.flux,ti,stage=self.stage)
		self.publish("FNaG",self.goldman.flux_Na,ti,stage=self.stage)
		self.publish("FKG",self.goldman.flux_K,ti,stage=self.stage)
		self.publish("FAG",self.goldman.flux_A,ti,stage=self.stage)
		self.publish("FHG",self.goldman.flux_H,ti,stage=self.stage)

	def systemparoutput(self):
		print
		print
		print
		print "SYSTEM PARAMETERS"
		print "{:30s}{:7s}       {:20s}".format("PARAMETER","VALUE","UNITS")
		print "{:30s}{:+5.2e}     {:20s}".format('a',self.A_1,'Eq/(mole.pH unit)')
		print "{:30s}{:+5.2e}     {:20s}".format('b',self.A_2,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('c',self.A_3,'---')
		temp_string = "pI(T={}oC)".format(self.temp_celsius)
		self.I_74=self.pit0-(0.016*self.temp_celsius)
		print "{:30s}{:+5.2e}     {:20s}".format(temp_string,self.I_74,'---')
		if self.BufferType == 'HEPES':
			temp_string = "pKa-HEPES(T={}oC)".format(self.temp_celsius)
			print "{:30s}{:+5.2e}     {:20s}".format(temp_string,self.pkhepes,'M')
		else:
			temp_string = "pKa-buffer(T={}oC)".format(self.temp_celsius)
			print "{:30s}{:+5.2e}     {:20s}".format(temp_string,self.pka,'M')
		print "{:30s}{:+5.2e}     {:20s}".format('fDt',self.integration_interval_factor,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('nX',self.A_10,'Eq/mole')
		print "{:30s}{:+5.2e}     {:20s}".format('KmNa',self.napump.B_1,'mM')
		print "{:30s}{:+5.2e}     {:20s}".format('KINa',self.napump.B_2,'mM')
		print "{:30s}{:+5.2e}     {:20s}".format('KmK',self.napump.B_3,'mM')
		print "{:30s}{:+5.2e}     {:20s}".format('KIK',self.napump.B_4,'mM')
		print "{:30s}{:+5.2e}     {:20s}".format('d',self.cotransport.zero_factor,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('fG',self.fG,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('s(Na/K)',self.Na_to_K,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('T(oC)',self.temp_celsius,'oC')
		print "{:30s}{:+5.2e}     {:20s}".format('Q10P',self.napump.B_9,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('Q10P(T)(Vwo/Vw)',self.napump.I_17,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('Q10L',self.B_10,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('Q10L(T)(Vwo/Vw)',self.I_18,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('Fmax(Na,Forwards)',self.napump.P_1,'mmole/loch')
		print "{:30s}{:+5.2e}     {:20s}".format('Fmax(Na,Reverse)',self.napump.P_2,'mmole/loch')
		print "{:30s}{:+5.2e}     {:20s}".format('FNa(Forwards,RS)',self.napump.flux_fwd,'mmole/loch')
		print "{:30s}{:+5.2e}     {:20s}".format('FNa(Reverse,RS)',self.napump.flux_rev,'mmole/loch')
		print "CHECK ABOVE TWO"
		print "{:30s}{:+5.2e}     {:20s}".format('PLNa (Na:A)',self.carriermediated.permeability_Na,'1/h')
		print "{:30s}{:+5.2e}     {:20s}".format('PLK (K:A)',self.carriermediated.permeability_K,'1/h')
		print "{:30s}{:+5.2e}     {:20s}".format('PLA (Na:A+K:A)',self.carriermediated.permeability_Na+self.carriermediated.permeability_K,'1/h')
		print "{:30s}{:+5.2e}     {:20s}".format('PGNa',self.goldman.permeability_Na,'1/h')
		print "{:30s}{:+5.2e}     {:20s}".format('PGK',self.goldman.permeability_K + self.goldman.P_11,'1/h')
		print "{:30s}{:+5.2e}     {:20s}".format('PGA',self.goldman.permeability_A,'1/h')
		print "{:30s}{:+5.2e}     {:20s}".format('PGH',self.goldman.permeability_H,'1/h')
		print "{:30s}{:+5.2e}     {:20s}".format('P(Na:K:2Cl)',self.cotransport.permeability,'1/h')
		print "{:30s}{:+5.2e}     {:20s}".format('P(H:A,J-S-equivalent)',self.JS.permeability,'1/h')
		print "{:30s}{:+5.2e}     {:20s}".format('PW',self.water.permeability,'l/((mmole/l)loch)')
		print "{:30s}{:+5.2e}     {:20s}".format('K0(Mg-buff)',self.q0,'mmole/loc')
		print "{:30s}{:+5.2e}     {:20s}".format('ATP+P(Mg)',self.atp,'mmole/loc')
		print "{:30s}{:+5.2e}     {:20s}".format('DPG+P(Mg)',self.dpgp,'mmole/loc')
		print "{:30s}{:+5.2e}     {:20s}".format('Alpha(Ca)',self.alpha,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('Additional Ca buffer',self.b1ca,'mmole/loc')
		print "{:30s}{:+5.2e}     {:20s}".format('KD of addl Ca buffer',self.b1cak,'mM')
		print "{:30s}{:+5.2e}     {:20s}".format('Benz2',self.benz2,'mmole/loc')
		print "{:30s}{:+5.2e}     {:20s}".format('Kd-Benz2',self.benz2k,'mM')

		print "{:30s}{:+5.2e}     {:20s}".format('FCaLmax',self.passiveCa.fcalm,'mmole/loc')
		print "{:30s}{:+5.2e}     {:20s}".format('Km CaLo',self.passiveCa.calk,'mM')
		print "{:30s}{:+5.2e}     {:20s}".format('KI CaLi',self.passiveCa.calik,'mM')
		
		print "{:30s}{:+5.2e}     {:20s}".format('FCaPmax',self.capump.fcapm,'mmole/loc')
		print "{:30s}{:+5.2e}     {:20s}".format('Km(Ca)-Cap',self.capump.capk,'mM')
		print "{:30s}{:+5.2e}     {:20s}".format('Hill-coeff-CaP',self.capump.h1,'---')

		print "{:30s}{:+5.2e}     {:20s}".format('Ca:(2-n)H (n=0,1,2)',self.capump.cah,'---')
		print "{:30s}{:+5.2e}     {:20s}".format('Km(Mg)-CaP',self.capump.capmgk,'mM')
		print "{:30s}{:+5.2e}     {:20s}".format('HKI(Hi)-CaP',self.capump.hik,'M')

		print "{:30s}{:+5.2e}     {:20s}".format('PK(Ca)max',self.goldman.pkm,'1/h')
		print "{:30s}{:+5.2e}     {:20s}".format('KKm(Ca)-PK(Ca)',self.goldman.pkcak,'mM')
		
		

		print
		print
		print
	def debugoutput(self):
		print
		print
		print "TIME (mins): {:f}".format(self.sampling_time*60.0)
		print
		print "{:40s}{:10s}{:10s}{:10s}{:10s}".format("Fluxes","Na","K","A","H");
		print "{:20s}{:20s}{:+5.2e}".format("Na Pump","Forwards",self.napump.flux_fwd)
		print "{:20s}{:20s}{:+5.2e}".format("","Reverse",self.napump.flux_rev)
		print "{:20s}{:20s}{:+5.2e} {:+5.2e}".format("","Net",self.napump.flux_net,self.napump.flux_K)
		print "{:20s}{:20s}{:+5.2e} {:+5.2e} {:+5.2e}".format("Na:Cl;K:cl","",self.carriermediated.flux_Na,self.carriermediated.flux_K,self.carriermediated.flux_Na+self.carriermediated.flux_K)
		print "{:20s}{:20s}{:+5.2e} {:+5.2e} {:+5.2e} {:+5.2e}".format("Electrodiffusional","",self.goldman.flux_Na,self.goldman.flux_K,self.goldman.flux_A,self.goldman.flux_H)
		print "{:20s}{:20s}{:10s}{:10s}{:+5.2e} {:+5.2e}".format("Jacobs-Stewart","","","",self.JS.flux_A,self.JS.flux_H)
		print "{:20s}{:20s}{:+5.2e} {:+5.2e} {:+5.2e}".format("Na:K:2Cl","",self.cotransport.flux_Na,self.cotransport.flux_K,self.cotransport.flux_A)
		print "{:20s}{:20s}{:10s}{:10s}{:10s}{:+5.2e}".format("Mg:2H(A23187) ","","","","",-2*self.a23.flux_Mg)
		print "{:20s}{:20s}{:10s}{:10s}{:10s}{:+5.2e}".format("JCa:2H(A23187)","","","","",-2*self.a23.flux_Ca)
		print "{:20s}{:20s}{:10s}{:10s}{:10s}{:+5.2e}".format("H through CaP","","","","",self.capump.flux_H)
		print "{:20s}{:20s}{:+5.2e} {:+5.2e} {:+5.2e} {:+5.2e}".format("Total net flux","",self.total_flux_Na,self.total_flux_K,self.total_flux_A,self.total_flux_H)
		print "------------------------------------------------------------------------------"
		print "{:40s}{:10s}{:10s}".format("","Mg","Ca")
		print "{:40s}{:+5.2e} {:+5.2e}".format("A23187",self.a23.flux_Mg,self.a23.flux_Ca)
		print "{:40s}{:10s}{:+5.2e}".format("PASSIVE Ca Flux","",self.passiveCa.flux)
		print "{:40s}{:10s}{:+5.2e}".format("Ca pump","",self.capump.flux_Ca)
		print "{:40s}{:+5.2e} {:+5.2e}".format("Total net flux",self.a23.flux_Mg,self.total_flux_Ca)
		print "------------------------------------------------------------------------------"
		print "{:40s}{:+5.2e}".format("Net water flux",self.water.flux)
		print "------------------------------------------------------------------------------"
		print "EN Error teat {:+5.2e}".format(self.total_flux_Na+self.total_flux_K-self.total_flux_A+self.total_flux_H+2*(self.total_flux_Ca+self.a23.flux_Mg))
		print "------------------------------------------------------------------------------"
		print "{:10s}{:10s}{:10s}{:10s}{:10s}".format("","CA","CC","MC","other")
		print "{:10s}{:+7.2f}   {:+7.2f}   {:+7.2f}   {:10s}{:+7.2f}".format("Na",self.cell.Na.amount,self.cell.Na.concentration,self.medium.Na.concentration,"Vw",self.Vw)
		print "{:10s}{:+7.2f}   {:+7.2f}   {:+7.2f}   {:10s}{:+7.2f}".format("K",self.cell.K.amount,self.cell.K.concentration,self.medium.K.concentration,"V/V",self.VV)
		print "{:10s}{:+7.2f}   {:+7.2f}   {:+7.2f}   {:10s}{:+7.2f}".format("A",self.cell.A.amount,self.cell.A.concentration,self.medium.A.concentration,"Ht",self.fraction*100.0)
		print "{:10s}{:7s}   {:+7.2e}   {:+7.2e}   {:10s}{:+7.2f}".format("H","",self.cell.H.concentration,self.medium.H.concentration,"MCHC",self.mchc)
		print "{:10s}{:+7.2f}   {:+7.2f}   {:7s}   {:10s}{:+10.4f}".format("XHb-",self.cell.XHbm.amount,self.cell.XHbm.concentration,"","Dens",self.density)
		print "{:10s}{:+7.2f}   {:+7.2f}   {:7s}   {:10s}{:+7.2f}".format("Hb",self.cell.Hb.amount,self.cell.Hb.concentration,"","E(mV)",self.Em)
		print "{:10s}{:+7.2f}   {:+7.2f}   {:7s}   {:10s}{:+7.2f}".format("X",self.cell.X.amount,self.cell.X.concentration,"","rA",self.rA)
		print "{:10s}{:+7.2f}   {:+7.2f}   {:+7.2f}   {:10s}{:+7.2f}".format("MgT",self.cell.Mgt.amount,self.cell.Mgt.concentration,self.medium.Mgt.concentration,"rH",self.rH)
		print "{:10s}{:+7.2f}   {:+7.2f}   {:+7.2f}   {:10s}{:+7.2f}".format("Os",self.cell.Os.amount,self.cell.Os.concentration,self.medium.Os.concentration,"fHb",self.fHb)
		print "{:10s}{:7s}   {:7s}   {:+7.2f}   {:10s}{:+7.2f}".format("BT","","",self.buffer_conc,"nHb",self.nHb)
		print "{:10s}{:7s}   {:7s}   {:+7.2f}   {:10s}{:+7.2f}".format("HB","","",self.medium.Hb.concentration,"pHC",self.cell.pH)
		print "{:10s}{:7s}   {:7s}   {:+7.2f}   {:10s}{:+7.2f}".format("Y(-)","","",self.medium.Gluconate.concentration,"pHM",self.medium.pH)
		V_14 = -self.goldman.rtoverf*math.log(self.medium.A.concentration/self.cell.A.concentration)
		print "{:10s}{:7s}   {:7s}   {:+7.2f}   {:10s}{:+7.2f}".format("Y(+)","","",self.medium.Glucamine.concentration,"Ea(mV)",V_14)
		V_13 = self.goldman.rtoverf*math.log(self.medium.H.concentration/self.cell.H.concentration)
		print "{:10s}{:7s}   {:7s}   {:+7.2f}   {:10s}{:+7.2f}".format("Sucrose","","",self.medium.Sucrose.concentration,"Eh(mV)",V_13)
		# V_15 = self.goldman.rtoverf *math.log(self.medium.K.concentration/self.cell.K.concentration)
		V_15 = -99
		print "{:10s}{:7s}   {:+7.2f}   {:7s}   {:10s}{:+7.2f}".format("Hb+/-","",self.cell.Hbpm.concentration,"","EK",V_15)
		V_16 = self.goldman.rtoverf*math.log(self.medium.Na.concentration/self.cell.Na.concentration)
		print "{:10s}{:7s}   {:+7.2f}   {:7s}   {:10s}{:+7.2f}".format("Cat","",self.cell.Cat.concentration,"","EK",V_16)
		enin = self.cell.Na.concentration + self.cell.K.concentration + 1000.0*self.cell.H.concentration + 2*(self.cell.Mgt.concentration + self.cell.Cat.concentration) + self.cell.XHbm.concentration - self.cell.A.concentration
		if self.BufferType == 'C':
			sumpos = self.medium.Na.concentration + self.medium.K.concentration + 1000.0*self.medium.H.concentration + self.medium.Hb.concentration + self.medium.Glucamine.concentration + 2*(self.medium.Mgf.concentration + self.medium.Caf.concentration)
			sumneg = self.medium.A.concentration + self.medium.Gluconate.concentration + self.edgneg
		else:
			sumpos = self.medium.Na.concentration + self.medium.K.concentration + 1000.0*self.medium.H.concentration + self.medium.Glucamine.concentration + 2*(self.medium.Mgf.concentration + self.medium.Caf.concentration)
			sumneg = self.medium.A.concentration + (self.buffer_conc-self.medium.Hb.concentration)+self.medium.Gluconate.concentration + self.edgneg

		enout = sumpos - sumneg
		print "------------------------------------------------------------------------------"
		print "EN error test: {:7.2e}    {:7.2e}".format(enin,enout)
		print "------------------------------------------------------------------------------"
		print "Cell Mg Values"
		print "{:15s}{:15s}{:15s}{:15s}".format("Mg2+(/lcw)","MgBT(/lc)","MgATP+P(/lc)","MgDPG+P(/lc)")
		print "{:+7.2f}       {:+7.2f}       {:+7.2f}       {:+7.2f}  ".format(self.cell.Mgf.concentration,self.mgb,(self.atp/self.VV)*self.cell.Mgf.concentration/(0.08+self.cell.Mgf.concentration),(self.dpgp/self.VV)*self.cell.Mgf.concentration/(3.6+self.cell.Mgf.concentration))
		print "------------------------------------------------------------------------------"
		print "Cell Calcium Values"
		print "{:15s}{:15s}{:15s}{:15s}{:15s}".format("CaT(/lc)","Ca2+(/lcw)","CaB(/lc)","CaB1(/lc)","Alpha(/lc)","CaB2(/lc)")
		print "{:+7.2e}       {:+7.2e}       {:7.2e}      {:+7.2e}       {:+7.2e}  ".format(self.cell.Cat.amount,self.cell.Caf.concentration,self.cab,
			(self.b1ca/self.VV)*(self.cell.Caf.concentration/(self.b1cak+self.cell.Caf.concentration)),
			self.cell.Caf.concentration*(self.alpha**(-1)-(self.Vw/(self.Vw+self.hb_content/136.0))),
			(self.benz2/self.VV)*(self.cell.Caf.concentration/(self.benz2k+self.cell.Caf.concentration)))
		print "------------------------------------------------------------------------------"
		print "Medium Mg Values"
		print "{:15s}{:15s}".format("MgoT","Mg2+o")
		print "{:+7.2f}       {:+7.2f}       {:7.2f} ".format(self.medium.Mgt.concentration,self.medium.Mgf.concentration,self.edgmg)

		print "------------------------------------------------------------------------------"
		print "Medium Calcium Values"
		print "{:15s}{:15s}".format("CaoT","Ca2+o")
		print "{:+7.2f}       {:+7.2f}       {:7.2f} ".format(self.medium.Cat.concentration,self.medium.Caf.concentration,self.edgca)
		print "------------------------------------------------------------------------------"
		print "Medium H-ligand Values"
		print "Need to make this woro for other ligand options..."
		print "{:15s}{:15s}{:15s}{:15s}{:15s}{:15s}".format("EGTAoT","EG4-","H-EG3-","H2-EG2-","EGTA-Mg","EGTA-Ca")
		print "{:+7.2e}       {:+7.2e}       {:+7.2e}      {:+7.2e}       {:+7.2e}       {:+7.2e}  ".format(self.edgto,self.edg4,self.edg3,self.edg2,self.edgmg,self.edgca)



		raw_input("Hit a key")


	# RS option methods

	def napumpscreenRS(self,rsoptions,used_options = []):
		print
		print "*****************************"
		print "Setting Na Pump RS parameters"
		print "*****************************"
		if 'na-efflux-fwd' in rsoptions:
			used_options.append('na-efflux-fwd')	
					
		self.napump.flux_fwd = rsoptions.get('na-efflux-fwd',self.napump.flux_fwd)

		print "Na efflux (forward) in RS (in mmol/loch, default=-2.61): {:f}".format(self.napump.flux_fwd)
		if self.napump.flux_fwd == -2.61:
			self.napmaxf = 1.0
		else:
			self.napmaxf = 0.0
		if 'na-influx-rev' in rsoptions:
			used_options.append('na-influx-rev')

		self.napump.flux_rev = rsoptions.get('na-influx-rev',self.napump.flux_rev)
		
		print "Na influx (reverse) in RS (in mmol/loch, default=0.0015): {:f}".format(self.napump.flux_rev)
		if self.napump.flux_rev == 0.0015:
			self.napmaxr = 1.0
		else:
			self.napmaxr = 0.0

		# Following two are never used?
		# self.I_31 = self.napump.flux_fwd
		# self.I_32 = self.flux_Napump_rev
		# TODO ADD TEXT HERE
		if 'km-na-inside' in rsoptions:
			self.napump.B_1 = rsoptions.get('km-na-inside')
			print "Km for Na inside (in mM, default=0.2): {:f}".format(self.napump.B_1)
			used_options.append('km-na-inside')
		if 'ki-k-inside' in rsoptions:
			self.napump.B_4 = rsoptions.get('ki-k-inside')
			print "KI for K inside (in mM, default=8.3):  {:f}".format(self.napump.B_4)
			used_options.append('ki-k-inside')
		if 'km-k-outside' in rsoptions:
			self.napump.B_3 = rsoptions.get('km-k-outside')
			print "Km for K outside (in mM, default=0.1):  {:f}".format(self.napump.B_3) 
			used_options.append('km-k-outside')
		if 'ki-na-outside' in rsoptions:
			self.napump.B_2 = rsoptions.get('ki-na-outside')
			print "KI for Na outside (in mM, default=18):  {:f}".format(self.napump.B_2)
			used_options.append('ki-na-outside')

		if 'inside-nai' in rsoptions:
			self.cell.Na.concentration = rsoptions.get('inside-nai')
			print "Initial [Na]i (in nM, default=10): {:f}".format(self.cell.Na.concentration)
			used_options.append('inside-nai')
		if 'inside-ki' in rsoptions:
			self.cell.K.concentration = rsoptions.get('inside-ki')
			print "Initial [K]i (in nM, default=140): {:f}".format(self.cell.K.concentration)	
			used_options.append('inside-ki')

		if 'q10-passive-pathway' in rsoptions:
			self.B_10 = rsoptions.get('q10-passive-pathway')
			print "Q10 of passive permeability pathways (default=2): {:f}".format(self.B_10)
			used_options.append('q10-passive-pathway')

		if 'q10-pumps' in rsoptions:
			self.napump.B_9 = rsoptions.get('q10-pumps')
			print "Q10 of pumps (default=4): {:f}".format(self.napump.B_9)
			used_options.appedn('q10-pumps')

		if 'fg' in rsoptions:
			self.fG = rsoptions.get('fg')
			print "Fraction of Goldman-defined passive Na and K flux (default=0.1): {:f}".format(self.fG)
			used_options.append('fg')


		print
		print


	def cellwaterscreenRS(self,rsoptions,used_options = []):
		print
		print "********************************"
		print "Setting Cell Water RS parameters"
		print "********************************"
		if 'hb-content' in rsoptions:
			used_options.append('hb-content')

		self.hb_content = rsoptions.get('hb-content',self.hb_content)
		used_options.append('hb-content')
		print "Hb content (default=34 g/dl): {:f}".format(self.hb_content)
		self.cell.Hb.amount = self.hb_content * 10.0/64.5
		# self.Q_9 = self.cell.Hb.amount
		self.I_79 = 1.0-self.hb_content/136.0
		self.vlysis = 1.45
		if self.hb_content != 34:
			print
			print
			return
		else:
			if 'cell-water' in rsoptions:
				used_options.append('cell-water')
			if 'lytic-cell-water' in rsoptions:
				used_options.append('lytic-cell-water')
			self.I_79 = rsoptions.get('cell-water',self.I_79)
			print "RS Cell water (default=0.75 l/340gHb): {:f}".format(self.I_79)
			self.vlysis = rsoptions.get('lytic-cell-water',self.vlysis)
			print "Lytic cell water (default=1.45 l/340gHb): {:f}".format(self.vlysis)
		print
		print

	def cellanionprotonscreenRS(self,rsoptions,used_options = []):
		print
		print "*************************************"
		print "Setting Cell Anion and Proton options"
		print "*************************************"
		if 'cla-conc' in rsoptions:
			self.cell.A.concentration = rsoptions.get('cla-conc')
			print "Intracellular Cl(A) concentration (default=95 mmole/lcw): {:f}".format(self.cell.A.concentration)
			used_options.append('cla-conc')
		print
		print


	def chargeandpiscreenRS(self,rsoptions,used_options = []):
		print "*****************************"
		print "Setting Charge and pH options"
		print "*****************************"
		if 'a' in rsoptions:
			self.A_1 = rsoptions.get('a')
			print "a (default=-10): {:f}".format(self.A_1)
			used_options.append('a')
		if 'pi' in rsoptions:
			self.pit0 = rsoptions.get('pi')
			print "pi(T=0) (default=7.2): {:f}".format(self.pit0)
			used_options.append('pi')
		print
		print



	def	setmgdefaults(self):
		# self.a23camk = 10.0
		# self.a23mgmk = 10.0
		# self.a23caik = 10.0
		# self.a23mgik = 10.0
		self.cell.Mgt.amount = 2.5
		self.medium.Mgt.concentration = 0.2

		# This is only used in output of system parameters?
		# q0=0.05
		self.q0 = 0.05

		self.atp = 1.2
		self.dpgp = 15
		self.VV = (1.0 - self.A_11) + self.Vw
		
		self.cell.Mgf.concentration,L = self.newton_raphson(self.eqmg,0.02,step = 0.0001,stop = 0.00001)
		# following is set in A23187
		# self.pmg = 0.01

	def setcadefaults(self):
		self.cell.Cat.amount = 0.000580
		self.cell.Cat.concentration = self.cell.Cat.amount/self.Vw
		self.medium.Cat.concentration = 1.0
		self.alpha = 0.30
		self.b1ca = 0.026
		self.b1cak = 0.014
		self.benz2 = 0.0
		self.benz2k = 5e-5
		self.cell.Caf.concentration = 1.12e-4
		
		# following now in passive object
		# self.fcal = 0.03 pasive.flux
		# self.fcalm = 0.050 
		# self.fcap = -0.03
		# self.fcapm = 12.0
		# self.calk = 0.8
		# self.calik = 0.0002
		# self.pkm = 30
		# self.pkcak = 1e-2
		# self.capk = 2e-4
		# self.h1 = 4
		# self.capmgk = 0.100
		# self.capmgki = 7.0
		# These two lines are now set in the napump object
		# self.napump.mgnapk = 0.05
		# self.napump.mgnapik = 4.0
		# self.a23.permeability_Ca = self.a23.permeability_Mg
		# self.hik = 4e-7
		# self.cah = 1.0
		self.edgto = 0.0
		self.medium.Mgf.concentration = self.medium.Mgt.concentration
		self.medium.Caf.concentration = self.medium.Cat.concentration

	def mgbufferscreenRS(self,rsoptions,used_options = []):
		print
		print "********************************"
		print "Setting Cytoplasmic Mg buffering"
		print "********************************"
		
		if 'mgot-medium' in rsoptions:
			used_options.append('mgot-medium')
		self.medium.Mgt.concentration = rsoptions.get('mgot-medium',0.2)
		print "MgoT (default=0.2 mM): {:f}".format(self.medium.Mgt.concentration)

		if 'mgit' in rsoptions:
			used_options.append('mgit')
		self.cell.Mgt.amount = rsoptions.get('mgit',2.5)
		print "MgiT (default = 2.5mmole/loc): {:f}".format(self.cell.Mgt.amount)

		if 'hab' in rsoptions:
			used_options.append('hab')
		self.mgb0 = rsoptions.get("hab",0.05)
		print "High-affinity buffer (default = 0.05 mmole/loc): {:f}".format(self.mgb0)

		if 'atpp' in rsoptions:
			used_options.append('atpp')
		self.atp = rsoptions.get('atpp',1.2)
		print "ATP+P (default = 1.2 mmole/loc): {:f}".format(self.atp)

		if '23dpg' in rsoptions:
			used_options.append('23dpg')
		self.dpgp = rsoptions.get('23dpg',15.0)
		print "2,3-DPG and other organic phosphates (default = 15 mEq P/loc): {:f}".format(self.dpgp)

		# self.mgnr()
		self.cell.Mgf.concentration,L = self.newton_raphson(self.eqmg,0.02,step = 0.0001,stop = 0.00001)

		print
		print

	def cabufferscreenRS(self,rsoptions,used_options = []):
		print
		print "*********"
		print "Ca Screen"
		print "*********"
		
		if 'caot-medium' in rsoptions:
			used_options.append('caot-medium')
		self.medium.Cat.concentration = rsoptions.get('caot-medium',1.0)
		print "CaoT (default=1mM): {:f}".format(self.medium.Cat.concentration)

		if 'add-ca-buffer' in rsoptions:
			used_options.append('add-ca-buffer')
		self.b1ca = rsoptions.get('add-ca-buffer',0.026)
		print "Additional cell Ca buffer (default=0.026 mmole/loc): {:f}".format(self.b1ca)
		
		if 'kd-of-ca-buffer' in rsoptions:
			used_options.append('kd-of-ca-buffer')
		self.b1cak = rsoptions.get('kd-of-ca-buffer',0.014)
		print "KD of additional cell Ca buffer (default=0.014mM): {:f}".format(self.b1cak)
		
		if 'alpha' in rsoptions:
			used_options.append('alpha')
		self.alpha = rsoptions.get('alpha',0.3)
		print "Alpha (default=0.3): {:f}".format(self.alpha)
		
		if 'benz2loaded' in rsoptions:
			used_options.append('benz2loaded')
		self.benz2 = rsoptions.get('benz2loaded',0.0)
		self.cbenz2 = self.benz2/self.Vw
		print "Benz2 loaded (default=0 mmole/loc): {:f}".format(self.benz2)
		
		if 'vmax-cap' in rsoptions:
			used_options.append('vmax-cap')
		self.capump.fcapm = rsoptions.get('vmax-cap',12.0)
		print "Vmax of Ca pump (default = 12 mmole/loc): {:f}".format(self.capump.fcapm)
		
		if 'k1/2' in rsoptions:
			used_options.append('k1/2')
		self.capump.capk = rsoptions.get('k1/2',2e-4)
		print "K1/2 of pump (default = 2e-4mM): {:f}".format(self.capump.capk)
		
		if 'hills' in rsoptions:
			used_options.append('hills')
		self.capump.h1 = rsoptions.get('hills',4.0)
		print "Hills coefficient of pump (h) (default = 4): {:f}".format(self.capump.h1)
		
		if 'pump-electro' in rsoptions:
			used_options.append('pump-electro')
		capstoich = int(rsoptions.get('pump-electro',0))
		print 'Pump electrogenicity: n=0, fully electrogenic (default); n=1, CaxH; n=2, Cax2H, electroneutral: {:f}'.format(capstoich)
		self.capump.cah = 2-capstoich


		if 'h+ki' in rsoptions:
			used_options.append('h+ki')
		self.capump.hik = rsoptions.get('h+ki',4e-7)
		print "Inhibition by internal H+ (default = 4e-7M): {:f}".format(self.capump.hik)
		
		if 'Mg2+K1/2' in rsoptions:
			used_options.append('Mg2+K1/2')
		self.capump.capmgk = rsoptions.get('Mg2+K1/2',0.1)
		print "[Mg2+]i-activation of pump (default K1/2=0.10 mM): {:f}".format(self.capump.capmgk)
		
		if 'pmax-pcag' in rsoptions:
			used_options.append('pmax-pcag')
		self.passiveCa.fcalm = rsoptions.get('pmax-pcag',0.05)
		print "Pmax of PCaG (default = 0.050 1/h): {:f}".format(self.passiveCa.fcalm)
		
		if 'ca2+-pkmax' in rsoptions:
			used_options.append('ca2+-pkmax')
		self.goldman.pkm = rsoptions.get('ca2+-pkmax',30.0)
		print "Ca2+-sensitive PKmax (default=30 1/h): {:f}".format(self.goldman.pkm)
		
		if 'ca2+-pkca' in rsoptions:
			used_options.append('ca2+-pkca')
		self.goldman.pkcak = rsoptions.get('ca2+-pkca',1e-2)
		print "Ca2+-sensitivity of PK(Ca) (default=1e-2Mm): {:f}".format(self.goldman.pkcak)


		if self.benz2 != 0:
			self.cell.Caf.concentration = 1e-8
			self.canr()

	def computehtRS(self):
		self.VV = (1-self.A_11) + self.Vw
		self.mchc = self.hb_content/self.VV
		self.density = (self.hb_content/100.0 + self.Vw)/self.VV

	def mediumconcentrationsRS(self):
		self.BufferType = "HEPES"
		self.medium.H.concentration = 10**(-self.medium.pH)
		self.pkhepes = 7.83 - 0.014*self.temp_celsius
		self.A_5 = 10**(-self.pkhepes)
		self.medium.Hb.concentration = self.buffer_conc*(self.medium.H.concentration/(self.A_5+self.medium.H.concentration))
		# Medium Na,K,or A concentration
		if self.medium.pH >= self.A_12:
			self.medium.Na.concentration = self.medium.A.concentration + self.edgneg + self.medium.Gluconate.concentration + (self.buffer_conc - self.medium.Hb.concentration) - self.medium.Glucamine.concentration - self.medium.K.concentration - 2*self.medium.Mgf.concentration - 2*self.medium.Caf.concentration
		else:
			self.medium.K.concentration = self.medium.A.concentration + self.edgneg + self.medium.Gluconate.concentration + (self.buffer_conc - self.medium.Hb.concentration) - self.medium.Glucamine.concentration - self.medium.Na.concentration - 2*self.medium.Mgf.concentration - 2*self.medium.Caf.concentration

		# 'Medium pH adjusted with NaOH
		# edgneg=2*edg2+3*edg3+4*edg4+2*(edgca+edgmg)
		# SDR - I don't get how the previous line ever happens

		# 890 M(1)=M(3)+edgneg+M(7)+(M(5)-m(6))-M(9)-M(2)-2*mgfo-2*cafo:GOTO 950
		# SDR - set edgneg to zero by checking value in original code
		# 'Medium pH adjusted with KOH
		# 910 M(2)=M(3)+edgneg+M(7)+(M(5)-m(6))-M(9)-M(1)-2*mgfo-2*cafo:GOTO 950
		# 'Medium pH adjusted with HCl
		# 930 M(3)=M(1)+M(2)+M(9)-(M(5)-m(6))-M(7)-edgneg+2*mgfo+2*cafo
		# 950 IF R=0 OR R=2 THEN RETURN
		# R = 1 here so 1010 it is
		# 970 IF R=3 THEN 990 ELSE 1010
		# 990 R=0
		# GOTO 4170

	def cellhphetc(self):
		# Cell H,pH,ratios,and E
		self.rA = self.medium.A.concentration/self.cell.A.concentration
		self.rH = self.rA
		self.cell.H.concentration = self.rH*self.medium.H.concentration
		self.cell.pH = -math.log10(self.cell.H.concentration)
		self.Em = -(8.615600000000004e-02)*(273 + self.temp_celsius)*math.log(self.rA)

		# Osmotic coeficient of Hb
		self.fHb = 1 + self.A_2*self.cell.Hb.amount/self.Vw + self.A_3*(self.cell.Hb.amount/self.Vw)**2
		# physiological pI at 37oC;
		self.I_74 = self.pit0 - (0.016*37)
		# net charge on Hb (Eq/mole)
		self.nHb = self.A_1*(self.cell.pH - self.I_74)

	def nakamountsmgcaconcRS(self):
		# Cell amounts of Na,K,and A, and concentrations of Mg and Ca
		self.cell.Na.amount = self.cell.Na.concentration*self.Vw
		self.cell.K.amount = self.cell.K.concentration*self.Vw
		self.cell.A.amount = self.cell.A.concentration*self.Vw
		self.cell.Mgt.concentration = self.cell.Mgt.amount/self.Vw
		self.cell.Cat.concentration = self.cell.Cat.amount/self.Vw


	def secureisotonicityRS(self):
		# ecures initial isotonicity and electroneutrality; it computes the
		# QX and nX required for initial osmotic and charge balance.  Since the Mg and
		# Ca buffers are within X, only the unbound forms of Mg and Ca participate in
		# osmotic equilibria within the cell.
		summ = self.medium.Na.concentration + self.medium.K.concentration + self.medium.A.concentration + self.buffer_conc + self.medium.Gluconate.concentration + self.medium.Glucamine.concentration + self.medium.Sucrose.concentration + self.medium.Mgt.concentration + self.medium.Cat.concentration
		sumq = self.cell.Na.amount + self.cell.K.amount + self.cell.A.amount + (self.cell.Mgf.concentration+self.cell.Caf.concentration)*self.Vw + self.fHb*self.cell.Hb.amount + self.benz2
		self.cell.X.amount = self.Vw*summ - sumq

	def fluxesRS(self):
		# Flux-rates and RS fluxes
		self.napump.compute_permeabilities(self.temp_celsius)

		self.carriermediated.flux_Na = -(1-self.fG)*self.napump.flux_net
		self.carriermediated.flux_K = -self.carriermediated.flux_Na/self.Na_to_K
		self.carriermediated.compute_permeabilities()

		# A fluxes through Na:Cl and K:Cl
		fal = self.carriermediated.flux_Na + self.carriermediated.flux_K
		# G-flux of A required to balance fal
		self.goldman.flux_A = -fal
		# G-rates and RS fluxes
		self.goldman.flux_Na = -self.fG*self.napump.flux_net
		self.goldman.flux_K = -self.goldman.flux_Na/self.Na_to_K
		self.I_18 = 1
		self.goldman.compute_permeabilities(self.Em,self.temp_celsius)

		# Zero-factor for cotransport
		self.cotransport.compute_zero_factor()
		self.X_6 = self.Vw
		# self.cotransportmediatedfluxes()
		self.cotransport.compute_flux(self.I_18)
		self.totalionfluxes()

		self.Q_8 = self.fHb*self.cell.Hb.amount

		self.chbetc()

	def computeRS(self):
		# RS-derived values
		self.medium.A.concentration = self.medium.A.concentration + 2*(self.medium.Mgt.concentration + self.medium.Cat.concentration)

		self.computehtRS()

		self.A_7 = self.fraction
		self.A_8 = self.A_7/(1-self.A_7)
		# 750 IF R=0 OR R=2 OR R=3 THEN RETURN  - SDR - why?
		
		self.mediumconcentrationsRS()
		self.cellhphetc()
		
		self.nakamountsmgcaconcRS()

		# Computes Q_X
		self.secureisotonicityRS()
		
		# Computes n_X
		# Non-protonizable charge on X (nX)
		self.A_10 = (self.cell.A.amount + 2*self.benz2 - (self.cell.Na.amount + self.cell.K.amount + 2*self.cell.Mgt.amount + 2*self.cell.Cat.amount+self.nHb*self.cell.Hb.amount))/self.cell.X.amount
		
		# Net charge on Hb
		self.Q_4 = self.nHb*self.cell.Hb.amount

		self.fluxesRS()	

	def write_csv(self,sl,outfile="out.txt"):
		# Order in csv file
		with open(outfile,'w') as f:
			# write the headings
			for i,p in enumerate(self.publish_order):
				if i>0:
					f.write('\t')
				f.write(p)
			f.write("\n")
			tim = sl.vals.get("Time")
			for i,t in enumerate(tim):
				for j,p in enumerate(self.publish_order):
					if j>0:
						f.write("\t")
					f.write("{:7.3f}".format(sl.vals.get(p)[i]))
				f.write("\n")
		f.close()

	def write_json(self,sl,outfile="out.json"):
		with open(outfile,'w') as f:
			json.dump(sl.vals,f)
		f.close()

	def check_results(self,sl,infile="testout.json"):
		# compare published values with json files for consistency
		print "Testing current output with {:s}".format(infile)
		nError = 0
		with open(infile,'r') as f:
			testres = json.load(f)
		for k in sl.vals.keys():
			tv = sl.vals.get(k)
			if k in testres:
				ov = testres.get(k)
				for i,t in enumerate(tv):
					if math.fabs(t-ov[i])>1e-6:
						print "ERROR with key {kn:s} at position {position:g}".format(kn=k,position=i)
						nError += 1
			else:
				print "{mk} not in stored results".format(mk=k)
		print "There were {:g} errors".format(nError)

if __name__ == "__main__":
	from simon_listener import Listener
	if len(sys.argv)>1:
		options = {}
		options['outfile'] = sys.argv[1].split('.')[0]
		with open(sys.argv[1],'r') as f:
			for line in f:
				a = line.rstrip().split(':')
				if a[0] != 'outfile':
					options[a[0]] = float(a[1])
				else:
					options[a[0]] = a[1]

	else:
		options = {'pgk':30,'pga':50,'time':30,'outfile':'pgk30pga50'}
		options = {'time':120,'fraction':0.1,'caot':0.2,'pmg':2e18}
	
	print options
	
	# Following RS options fix issues in original code with bad defaults
	rsoptions = {}
	rsoptions['pump-electro'] = 1
	rsoptions['hab'] = 0
	options['pump-electro'] = 1
	options['hab'] = 0
	options['cyclesperprint'] = 777
	# options['time'] = 0.02
	r = rbc_model()
	sl = Listener()
	# r.debug = True
	r.register(sl)
	usedoptions = []
	r.setup(options,usedoptions)

	r.setupds(options,usedoptions)

	# sys.exit(0)


	r.runall()

	r.write_csv(sl,outfile = "python_test.csv")

	sys.exit(0)
	if 'outfile' in options:
		outfile = options.get('outfile')
	else:
		outfile = 'out'
	try:
		r.write_csv(sl,outfile = "{:s}{:s}".format(outfile,'.txt'))
		r.write_json(sl,outfile = "{:s}{:s}".format(outfile,'.json'))
	except IOError as e:
		print "Unable to write output file"
		print e
	# r.check_results(sl)
