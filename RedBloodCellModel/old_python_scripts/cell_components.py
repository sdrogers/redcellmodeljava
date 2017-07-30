import math

class region(object):
	def __init__(self):
		self.Na = species()
		self.K = species()
		self.A = species()
		self.A.z = -1
		self.H = species()
		self.Hb = species()
		self.X = species()
		self.Os = species()
		self.Gluconate = species()
		self.Glucamine = species()
		self.Sucrose = species()
		self.Caf = species()
		self.Mgf = species()
		self.Cat = species()
		self.Mgt = species()
		self.XHbm = species()
		self.COs = species()
		# concentration of charge on Hb
		self.Hbpm = species()
		self.pH = 0.0


class species(object):
	def __init__(self):
		self.concentration = 0.0
		self.amount = 0.0
		self.z = 1

class napump(object):
	def __init__(self,cell,medium):
		self.P_1 = 0.0
		self.P_2 = 0.0
		self.Na_to_K = 1.5
		self.cell = cell
		self.medium = medium
		self.flux_fwd = -2.61
		self.flux_rev = 0.0015
		self.flux_net = self.flux_fwd + self.flux_rev
		self.flux_K = -self.flux_net/self.Na_to_K
		self.total_flux = self.flux_net + self.flux_K
		self.I_17 = 0.0
		self.B_1 = 0.2
		self.B_2 = 18.0
		self.B_3 = 0.1
		self.B_4 = 8.3
		self.mgnap = 0.0
		self.phnap = 0.0
		self.I_3 = 0.0
		self.I_6 = 0.0
		self.I_9 = 0.0
		self.I_11 = 0.0
		self.B_9 = 4.0
		self.mgnapk = 0.05
		self.mgnapik = 4.0

		self.I_77 = 7.216
		self.I_78 = 0.4



	def compute_I(self,temperature):
		I_1 = self.B_1*(1 + (self.cell.K.concentration/self.B_4))
		I_2 = self.cell.Na.concentration/(self.cell.Na.concentration + I_1)
		self.I_3 = I_2**3
		I_4 = self.B_3 * (1 + self.medium.Na.concentration/self.B_2)
		I_5 = self.medium.K.concentration/(self.medium.K.concentration + I_4)
		self.I_6 = I_5**2

		I_7 = self.B_4*(1 + self.cell.Na.concentration/self.B_1)
		I_8 = self.cell.K.concentration/(self.cell.K.concentration + I_7)
		self.I_9 = I_8**2
		I_10 = self.medium.Na.concentration/(self.medium.Na.concentration + self.B_2*(1 + self.medium.K.concentration/self.B_3))
		self.I_11 = I_10**3

		self.I_17 = math.exp(((37.0-temperature)/10.0)*math.log(self.B_9))


	def compute_permeabilities(self,temperature):
		self.compute_I(temperature)
		self.compute_mgnap()
		self.compute_phnap()
		self.P_1 = math.fabs(self.flux_fwd/(self.mgnap*self.phnap*self.I_3*self.I_6))
		self.P_2 = math.fabs(self.flux_rev/((self.mgnap*self.phnap)*(self.I_9*self.I_11)))


	def compute_mgnap(self):
		self.mgnap = (self.cell.Mgf.concentration/(self.mgnapk + self.cell.Mgf.concentration))*(self.mgnapik/(self.mgnapik + self.cell.Mgf.concentration))

	def compute_phnap(self):
		self.phnap = math.exp(-((self.cell.pH - self.I_77)/self.I_78)**2)

	def compute_flux(self,temperature):
		self.compute_I(temperature)
		self.compute_mgnap()
		self.compute_phnap()
		self.flux_fwd = -(self.P_1/self.I_17)*self.mgnap*self.phnap*self.I_3*self.I_6
		self.flux_rev = (self.P_2/self.I_17)*self.mgnap*self.phnap*self.I_9*self.I_11
		self.flux_net = self.flux_fwd + self.flux_rev
		self.flux_K = -self.flux_net/self.Na_to_K
		self.total_flux = self.flux_net + self.flux_K

class JacobsStewart(object):
	def __init__(self,cell,medium):
		self.permeability = 2.5e8
		self.cell = cell
		self.medium = medium
		self.flux_A = 0.0
		self.flux_H = 0.0

	def compute_flux(self,I_18):
		I_13 = -(self.permeability/I_18)*(self.cell.A.concentration*self.cell.H.concentration-(self.medium.A.concentration*self.medium.H.concentration))
		self.flux_A = I_13
		self.flux_H = I_13

class Cotransport(object):
	def __init__(self,cell,medium):
		self.permeability = 0.000000001
		self.flux_A = 0
		self.flux_Na = 0
		self.flux_K = 0
		self.cell = cell
		self.medium = medium
		self.zero_factor = 0.0

	def compute_zero_factor(self):
		self.zero_factor = (self.cell.Na.concentration*self.cell.K.concentration*self.cell.A.concentration**2)/(self.medium.Na.concentration*self.medium.K.concentration*self.medium.A.concentration**2)

	def compute_flux(self,I_18):
		I_12 = -(self.permeability/I_18)*(self.cell.A.concentration*self.cell.A.concentration*self.cell.Na.concentration*self.cell.K.concentration-self.zero_factor*(self.medium.A.concentration*self.medium.A.concentration*self.medium.Na.concentration*self.medium.K.concentration))
		self.flux_A = 2*I_12
		self.flux_Na = I_12
		self.flux_K = I_12

class CarrierMediated(object):
	def __init__(self,cell,medium):
		self.cell = cell
		self.medium = medium
		self.flux_Na = 0.0
		self.flux_K = 0.0
		# P_3
		self.permeability_Na = 0.0
		# P_5
		self.permeability_K = 0.0
		self.flux_K = 0.0
		self.flux_Na = 0.0

	def compute_permeabilities(self):
		self.permeability_Na = math.fabs(self.flux_Na/(self.cell.Na.concentration*self.cell.A.concentration-self.medium.Na.concentration*self.medium.A.concentration))
		self.permeability_K = math.fabs(self.flux_K/(self.cell.K.concentration*self.cell.A.concentration-self.medium.K.concentration*self.medium.A.concentration))

	def compute_flux(self,I_18):
		self.flux_Na = -(self.permeability_Na/I_18)*(self.cell.Na.concentration*self.cell.A.concentration-self.medium.Na.concentration*self.medium.A.concentration)
		self.flux_K = -(self.permeability_K/I_18)*(self.cell.K.concentration*self.cell.A.concentration-self.medium.K.concentration*self.medium.A.concentration)

class Goldman(object):
	def __init__(self,cell,medium):
		self.cell = cell
		self.medium = medium
		self.permeability_Na = 0.0
		self.permeability_A = 1.2
		self.permeability_H = 2e-10
		self.permeability_K = 0.0

		self.flux_Na = 0.0
		self.flux_A = 0.0
		self.flux_H = 0.0
		self.flux_K = 0.0

		self.Goldman_factor = 0.0
		self.P_11 = 0.0

		self.pkm = 30.0
		self.pkcak = 1e-2
		self.pgkh = 0.0

	def gfactors(self,Em,temperature):
		# 7410 G-factors
		# fluxes are defined positive when into the cells;
		# The sign of the G-components is thus inverted:
		# 8.6156e-2 is R/F times 1e-3 (perhaps concentrations in mmol?)
		self.rtoverf = ((8.6156e-2)*(273+temperature))
		foverrt = 1.0/((8.6156e-2)*(273+temperature))
		self.Goldman_factor = Em*foverrt

	def compute_permeabilities(self,Em,temperature):
		self.gfactors(Em,temperature)
		self.permeability_Na = math.fabs(self.flux_Na/self.gflux(self.cell.Na,self.medium.Na))
		self.permeability_K = math.fabs(self.flux_K/self.gflux(self.cell.K,self.medium.K))

	def total_G_permeability_K(self):
		I_62 = 1.0/(1.0+ (self.cell.H.concentration**4)/2.5e-30)
		self.P_11 = self.pgkh*I_62
		P_6 = self.permeability_K + self.pkm*(self.cell.Caf.concentration**4/(self.pkcak**4 + self.cell.Caf.concentration**4))
		return P_6 + self.P_11


	def compute_flux(self,Em,temperature,I_18):
		self.gfactors(Em,temperature)
		self.flux_Na = self.fullgflux(self.cell.Na,self.medium.Na,self.permeability_Na,I_18)
		self.flux_A = self.fullgflux(self.cell.A,self.medium.A,self.permeability_A,I_18)
		self.flux_H = self.fullgflux(self.cell.H,self.medium.H,self.permeability_H,I_18)
		self.flux_K = self.fullgflux(self.cell.K,self.medium.K,self.total_G_permeability_K(),I_18)

	def gflux(self,cell_species,medium_species):
		try:
			return -cell_species.z*self.Goldman_factor*(medium_species.concentration - cell_species.concentration*math.exp(cell_species.z*self.Goldman_factor))/(1-math.exp(cell_species.z*self.Goldman_factor))
		except OverflowError: 
			print "Overflow in exp: {}, EXITING".format(cell_species.z*self.Goldman_factor)
			import sys
			sys.exit(0)

	def fullgflux(self,cell_species,medium_species,permeability,I_18):
		return (permeability/I_18)*self.gflux(cell_species,medium_species)

class A23187(object):
	def __init__(self,cell,medium):
		self.cell = cell
		self.medium = medium
		self.camk = 10.0
		self.mgmk = 10.0
		self.caik = 10.0
		self.mgik = 10.0

		self.flux_Mg = 0.0
		self.flux_Ca = 0.0

		self.permeability_Mg = 0.01
		self.permeability_Ca = 0.01

	def compute_flux(self,I_18):
		mgcao=(self.medium.Mgf.concentration/(self.mgmk+(self.mgmk*self.medium.Caf.concentration/self.caik)+self.medium.Mgf.concentration))
		camgo=(self.medium.Caf.concentration/(self.camk+(self.camk*self.medium.Mgf.concentration/self.mgik)+self.medium.Caf.concentration))
		mgcai=(self.cell.Mgf.concentration/(self.mgmk+(self.mgmk*self.cell.Caf.concentration/self.caik)+self.cell.Mgf.concentration))
		camgi=(self.cell.Caf.concentration/(self.camk+(self.camk*self.cell.Mgf.concentration/self.mgik)+self.cell.Caf.concentration))
		self.flux_Mg = self.permeability_Mg/I_18*(mgcao*self.cell.H.concentration**2 - mgcai*self.medium.H.concentration**2)
		self.flux_Ca = self.permeability_Ca/I_18*(camgo*self.cell.H.concentration**2 - camgi*self.medium.H.concentration**2)

class WaterFlux(object):
	def __init__(self,cell,medium):
		self.cell = cell
		self.medium = medium
		self.flux = 0.0
		self.permeability = 2.0

	def compute_flux(self,fHb,cbenz2,buffer_conc,edgto,I_18):
		# self.I_29 = self.Vw
		self.cell.Os.concentration = self.cell.Na.concentration + self.cell.K.concentration + self.cell.A.concentration + fHb*self.cell.Hb.concentration + self.cell.X.concentration + self.cell.Mgf.concentration + self.cell.Caf.concentration + cbenz2
		I_26 = self.medium.Na.concentration + self.medium.K.concentration + self.medium.A.concentration + buffer_conc + self.medium.Gluconate.concentration + self.medium.Glucamine.concentration + self.medium.Sucrose.concentration + (self.medium.Mgf.concentration+self.medium.Caf.concentration+edgto)
		D_7 = self.cell.Os.concentration-I_26
		# Water flux
		self.flux = (self.permeability/I_18)*D_7

class PassiveCa(object):
	def __init__(self,cell,medium,goldman):
		self.cell = cell
		self.medium = medium
		# needs ref to goldman object for goldman_factor
		self.goldman = goldman
		self.flux = 0.03
		self.calk = 0.8
		self.calik = 0.0002
		self.fcalm = 0.050


	def compute_flux(self,I_18):
		calreg = (self.calik/(self.calik+self.cell.Caf.concentration))*(self.medium.Caf.concentration/(self.calk+self.medium.Caf.concentration))
		self.flux = -(self.fcalm/I_18)*calreg*2*self.goldman.Goldman_factor*((self.medium.Caf.concentration-self.cell.Caf.concentration*math.exp(2*self.goldman.Goldman_factor))/(1-math.exp(2*self.goldman.Goldman_factor)))

class CaPumpMg2(object):
	def __init__(self,cell,medium,napump):
		self.cell = cell
		self.medium = medium
		# needs napump for temp factor
		self.napump = napump
		self.hik = 4e-7
		self.capmgk = 0.1
		self.capmgki = 7.0
		self.fcapm = 12.0
		self.h1 = 4.0
		self.capk = 2e-4
		self.flux_Ca = -0.03
		self.flux_H = 0.0
		self.cah = 1.0


	def compute_flux(self):
		capmg = (self.cell.Mgf.concentration/(self.capmgk+self.cell.Mgf.concentration))*(self.capmgki/(self.capmgki+self.cell.Mgf.concentration))
		caphik=(self.hik/(self.hik+self.cell.H.concentration))
		fcapglobal=-(self.fcapm/self.napump.I_17)*capmg*caphik
		self.flux_Ca = fcapglobal*(self.cell.Caf.concentration**self.h1)/(self.capk**self.h1 + self.cell.Caf.concentration**self.h1)
		if self.cah == 1:
			self.flux_H = -self.flux_Ca
		if self.cah == 2:
			self.flux_H = 0.0
		if self.cah == 0:
			self.flux_H = -2.0*self.flux_Ca



