package br.ifpe.presentation;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import br.ifpe.entities.Account;
import br.ifpe.entities.Client;
import br.ifpe.service.BankService;
import br.ifpe.service.ServiceFactory;

/**
 * OBSERVAÇÕES: 
 * NÃO é permitido o uso de nenhuma estrutura de repetição (for, while, do-while).
 * Tente, ao máximo, evitar o uso das estruturas if, else if, else e switch-case
 * 
 * @author Victor Lira
 *
 */
public class Main {

	private static BankService service = ServiceFactory.getService();

	public static void main(String[] args) {
		//TODO to test here
	}

	/**
	 * 1. Imprima na tela o nome e e-mail de todos os clientes (sem repetição), usando o seguinte formato:
	 * Victor Lira - vl@cin.ufpe.br
	 */
	public static void imprimirNomesClientes() {
		service
		.listClients()
		.stream()
		.map(client -> client.getName() + " - " + client.getEmail())
		.distinct()
		.forEach(System.out::println);
	}

	/**
	 * 2. Imprima na tela o nome do cliente e a média do saldo de suas contas, ex:
	 * Victor Lira - 352
	 */
	public static void imprimirMediaSaldos() {
		service
		.listClients()
		.stream()
		.map(client -> client.getName() + " - " + service.listAccounts()
		.stream()
		.filter(account -> account.getClient().getName().equals(client.getName()))
		.mapToDouble(account -> account.getBalance())
		.average().getAsDouble())
		.forEach(System.out::println);

	}

	/**
	 * 3. Considerando que só existem os países "Brazil" e "United States", 
	 * imprima na tela qual deles possui o cliente mais rico, ou seja,
	 * com o maior saldo somando todas as suas contas.
	 */
	public static void imprimirPaisClienteMaisRico() {
		double sumClientBrazil =
				service
				.listAccounts()
				.stream()
				.filter(account -> account.getClient().getAddress().getCountry().equals("Brazil"))
				.mapToDouble(account -> account.getBalance())
				.sum();

		double sumClientUSA = 
				service
				.listAccounts()
				.stream()
				.filter(account -> account.getClient().getAddress().getCountry().equals("United States"))
				.mapToDouble(account -> account.getBalance())
				.sum();

		System.out.println(Double.compare(sumClientBrazil, sumClientUSA));
	}

	/**
	 * 4. Imprime na tela o saldo médio das contas da agência
	 * @param agency
	 */
	public static void imprimirSaldoMedio(int agency) {
		OptionalDouble average =
				service
				.listAccounts()
				.stream()
				.filter(account -> account.getAgency() == agency)
				.mapToDouble(account -> account.getBalance())
				.average();
		System.out.println(average);
	}

	/**
	 * 5. Imprime na tela o nome de todos os clientes que possuem conta poupança (tipo SAVING)
	 */
	public static void imprimirClientesComPoupanca() {
		service
		.listAccounts()
		.stream()
		.filter(account -> account.getType().equals("SAVING"))
		.map(account -> account.getClient())
		.distinct()
		.forEach(System.out::println);
	}

	/**
	 * 6.
	 * @param agency
	 * @return Retorna uma lista de Strings com o "estado" de todos os clientes da agência
	 */
	public static List<String> getEstadoClientes(int agency) {
		List<String> stateOfAllAgencyClients =
				service
				.listAccounts()
				.stream()
				.filter(account -> account.getAgency() == agency)
				.map(account -> account.getClient().getAddress().getState())
				.collect(Collectors.toList());

		return (List<String>) stateOfAllAgencyClients;
	}

	/**
	 * 7.
	 * @param country
	 * @return Retorna uma lista de inteiros com os números das contas daquele país
	 */
	public static int[] getNumerosContas(String country) {
		int[] countryAccountNumbers =
				service
				.listAccounts()
				.stream()
				.filter(account -> account.getClient().getAddress().getCountry().equals(country))
				.mapToInt(account -> account.getNumber()).toArray();

		return countryAccountNumbers;
	}

	/**
	 * 8.
	 * Retorna o somatório dos saldos das contas do cliente em questão 
	 * @param clientEmail
	 * @return
	 */
	public static double getMaiorSaldo(String clientEmail) {
		double sumBalance =
				service
				.listAccounts()
				.stream()
				.filter(account -> account.getClient().getEmail().equals(clientEmail))
				.mapToDouble(account -> account.getBalance())
				.sum();

		return sumBalance;
	}

	/**
	 * 9.
	 * Realiza uma operação de saque na conta de acordo com os parâmetros recebidos
	 * @param agency
	 * @param number
	 * @param value
	 */
	public static void sacar(int agency, int number, double value) {
		service
		.listAccounts()
		.stream()
		.filter(account -> account.getAgency() == agency && account.getNumber() == number)
		.map(account -> account.getBalance() - value);
	}

	/**
	 * 10. Realiza um deposito para todos os clientes do país em questão	
	 * @param country
	 * @param value
	 */
	public static void depositar(String country, double value) {
		service
		.listAccounts()
		.stream()
		.filter(account -> account.getClient().getAddress().getCountry().equals(country))
		.map(account -> (account.getBalance() + value));

	}

	/**
	 * 11. Realiza uma transferência entre duas contas de uma agência.
	 * @param agency - agência das duas contas
	 * @param numberSource - conta a ser debitado o dinheiro
	 * @param numberTarget - conta a ser creditado o dinheiro
	 * @param value - valor da transferência
	 */
	public static void transferir(int agency, int numberSource, int numberTarget, double value) {
		service
		.listAccounts()
		.stream()
		.filter(account -> (account.getAgency() == agency) && 
				(account.getNumber() == numberSource))
		.map(account -> (account.getBalance() - value));

		service
		.listAccounts()
		.stream()
		.filter(account -> (account.getAgency() == agency) && 
				(account.getNumber() == numberTarget))
		.map(account -> (account.getBalance() + value));
	}

	/**
	 * 12.
	 * @param clients
	 * @return Retorna uma lista com todas as contas conjuntas (JOINT) dos clientes
	 */
	public static List<Account> getContasConjuntas(List<Client> clients) {
		List<Account> jointAccounts = new ArrayList<Account>();
		service
		.listAccounts()
		.stream()
		.filter(account -> account.getClient().equals(clients))
		.map(account -> account.getType().equals("JOINT"))
		.collect(Collectors.toList());


		return jointAccounts;

	}

	/**
	 * 13.
	 * @param state
	 * @return Retorna uma lista com o somatório dos saldos de todas as contas do estado 
	 */
	public static double getSomaContasEstado(String state) {
		DoubleSummaryStatistics sumOfTheBalancesOfTheStateAccount = 
				service
				.listAccounts()
				.stream()
				.filter(account -> account.getClient().getAddress().getState().equals(state))
				.collect(Collectors.summarizingDouble(Account::getBalance));

		return sumOfTheBalancesOfTheStateAccount.getSum();
	}

	/**
	 * 14.
	 * @return Retorna um array com os e-mails de todos os clientes que possuem contas conjuntas
	 */
	public static String[] getEmailsClientesContasConjuntas() {
		String [] emailsFromCustomersWithJointAccounts =
				(String[]) service
				.listAccounts()
				.stream()
				.filter(accounts -> accounts.getType().equals("JOINT"))
				.map(accounts -> accounts.getClient().getEmail())
				.toArray();

		return emailsFromCustomersWithJointAccounts;
	}

	/**
	 * 15.
	 * @param number
	 * @return Retorna se o número é primo ou não
	 */
	public static boolean isPrimo(int number) {
		return IntStream
				.rangeClosed(2, (number/2))
				.noneMatch(i -> number % i == 0);
	}

	/**
	 * 16.
	 * @param number
	 * @return Retorna o fatorial do número
	 * 
	 */
	public static int getFatorial(int number) {
		int factorial =
				IntStream.rangeClosed(1, number)
				.reduce((n1, n2) -> (n1*n2)).orElse(1);

		return factorial;


	}
}
