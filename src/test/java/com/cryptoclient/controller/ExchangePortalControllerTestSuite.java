package com.cryptoclient.controller;

import com.cryptoclient.client.ApiService;
import com.cryptoclient.client.ServiceFactory;
import com.cryptoclient.client.nomics.NomicsClient;
import com.cryptoclient.domain.Currency;
import com.cryptoclient.domain.ExchangePortal;
import com.cryptoclient.dto.ExchangePortalDto;
import com.cryptoclient.mapper.ExchangePortalMapper;
import com.cryptoclient.service.interfaces.ExchangePortalService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ExchangePortalController.class)
@ActiveProfiles("test")
public class ExchangePortalControllerTestSuite {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceFactory serviceFactory;

    @MockBean
    private ExchangePortalMapper exchangePortalMapper;

    @MockBean
    private ExchangePortalService exchangePortalService;

    @MockBean
    private ApiService apiService;

    @MockBean
    private NomicsClient nomicsClient;

    @Test
    public void postExchangePortalTest() throws Exception {
        //Given
        ExchangePortal exchangePortal = ExchangePortal.builder()
                        .id(1L)
                        .provider("nomics")
                        .currencyToBuy(Currency.XMR)
                        .currencyToPay(Currency.USD)
                        .ratio(160.0)
                        .time(LocalDateTime.of(2020, 12, 20, 20, 50))
                        .build();

        ExchangePortalDto exchangePortalDto = new ExchangePortalDto(1L, "nomics", Currency.XMR,
                Currency.USD, 160.00, LocalDateTime.of(2020, 12, 20, 20, 50));
        Currency currency = Currency.XMR;
        String serviceName = "nomics";
        String currencyString = "XMR";

        when(apiService.createExchangePortal(currency)).thenReturn(exchangePortal);
        when(serviceFactory.createService(serviceName)).thenReturn(apiService);
        when(exchangePortalMapper.mapToExchangePortalDto(apiService.createExchangePortal(currency)))
                .thenReturn(exchangePortalDto);

        //When & Then
        mockMvc.perform(post("/v1/exchange").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("currency", currencyString)
                .param("serviceName", serviceName))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getEmptyExchangePortalsListTest() throws Exception {
        //Given
        List<ExchangePortalDto> exchangePortalDtoList = new ArrayList<>();
        when(exchangePortalMapper.mapToExchangePortalDtoList(exchangePortalService.getExchangePortals())).thenReturn(exchangePortalDtoList);
        //When & Then
        mockMvc.perform(get("/v1/exchange/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200)) // or isOk()
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    public void getExchangePortalTest() throws Exception {
        //Given
        ExchangePortal exchangePortal = ExchangePortal.builder()
                .id(1L)
                .provider("nomics")
                .currencyToBuy(Currency.XMR)
                .currencyToPay(Currency.USD)
                .ratio(160.0)
                .time(LocalDateTime.of(2020, 12, 20, 20, 50))
                .build();
        ExchangePortalDto exchangePortalDto = new ExchangePortalDto(1L, "nomics", Currency.XMR,
                Currency.USD, 160.00, LocalDateTime.of(2020, 12, 20, 20, 50));
        Currency currency = Currency.XMR;
        String serviceName = "nomics";
        String currencyString = "XMR";

        when(apiService.createExchangePortal(currency)).thenReturn(exchangePortal);
        when(serviceFactory.createService(serviceName)).thenReturn(apiService);
        when(exchangePortalMapper.mapToExchangePortalDto(apiService.createExchangePortal(currency)))
                .thenReturn(exchangePortalDto);

        //When & Then
        mockMvc.perform(post("/v1/exchange").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("currency", currencyString)
                .param("serviceName", serviceName))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.provider", is("nomics")))
                .andExpect(jsonPath("$.currencyToBuy", is("XMR")))
                .andExpect(jsonPath("$.currencyToPay", is("USD")))
                .andExpect(jsonPath("$.ratio", is(160.0)))
                .andExpect(jsonPath("$.time", is("2020-12-20T20:50:00")))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getExchangePortalByIdTest() throws Exception {
        //Given
        ExchangePortalDto exchangePortalDto = new ExchangePortalDto(1L, "nomics", Currency.XMR,
                Currency.USD, 160.00, LocalDateTime.of(2020, 12, 20, 20, 50));

        when(exchangePortalMapper.mapToExchangePortalDto(exchangePortalService.findExchangePortalById
                (5L))).thenReturn(exchangePortalDto);

        //When & Then
        mockMvc.perform(get("/v1/exchange/id/5").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.provider", is("nomics")))
                .andExpect(jsonPath("$.currencyToBuy", is("XMR")))
                .andExpect(jsonPath("$.currencyToPay", is("USD")))
                .andExpect(jsonPath("$.ratio", is(160.0)))
                .andExpect(jsonPath("$.time", is("2020-12-20T20:50:00")))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void deleteExchangePortalTest() throws Exception {
        //Given
        //When & Then
        mockMvc.perform(delete("/v1/exchange/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    public void getExchangePortalsByCurrencyTest() throws Exception {
        //Given
        ExchangePortalDto exchangePortalDto1 = new ExchangePortalDto(1L, "nomics", Currency.XMR,
                Currency.USD, 160.00, LocalDateTime.of(2020, 12, 20, 20, 50));
        ExchangePortalDto exchangePortalDto2 = new ExchangePortalDto(2L, "nomics", Currency.XMR,
                Currency.USD, 160.00, LocalDateTime.of(2020, 12, 20, 20, 50));

        Currency currency = Currency.XMR;

        List<ExchangePortalDto> exchangePortalDtoList = new ArrayList<>();
        exchangePortalDtoList.add(exchangePortalDto1);
        exchangePortalDtoList.add(exchangePortalDto2);

        when(exchangePortalMapper.mapToExchangePortalDtoList(exchangePortalService.
                getExchangePortalsWithCurrency(currency))).thenReturn(exchangePortalDtoList);

        //When & Then
        mockMvc.perform(get("/v1/exchange/currency").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("currency", "XMR"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].provider", is("nomics")))
                .andExpect(jsonPath("$[0].currencyToBuy", is("XMR")))
                .andExpect(jsonPath("$[0].currencyToPay", is("USD")))
                .andExpect(jsonPath("$[0].ratio", is(160.0)))
                .andExpect(jsonPath("$[0].time", is("2020-12-20T20:50:00")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].provider", is("nomics")))
                .andExpect(jsonPath("$[1].currencyToBuy", is("XMR")))
                .andExpect(jsonPath("$[1].currencyToPay", is("USD")))
                .andExpect(jsonPath("$[1].ratio", is(160.0)))
                .andExpect(jsonPath("$[1].time", is("2020-12-20T20:50:00")))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}
