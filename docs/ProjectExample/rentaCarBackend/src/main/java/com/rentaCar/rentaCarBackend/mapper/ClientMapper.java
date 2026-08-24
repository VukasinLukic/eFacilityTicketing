package com.rentaCar.rentaCarBackend.mapper;

import com.rentaCar.rentaCarBackend.dto.CityDTO;
import com.rentaCar.rentaCarBackend.dto.ClientDTO;
import com.rentaCar.rentaCarBackend.model.City;
import com.rentaCar.rentaCarBackend.model.Client;
import org.springframework.stereotype.Component;

@Component
// IZMENJENO: BaseMapper sada radi sa ClientDTO
public class ClientMapper implements BaseMapper<ClientDTO, Client> {

    @Override
    public Client toDomainEntity(ClientDTO clientDTO) {
        Client client = new Client();
        client.setJmbg(clientDTO.getJmbg());
        client.setName(clientDTO.getName());
        client.setSurname(clientDTO.getSurname());
        client.setAge(clientDTO.getAge());

        if (clientDTO.getCity() != null) {
            client.setCity(new City(clientDTO.getCity().getId(), clientDTO.getCity().getName()));
        } else {
            client.setCity(null);
        }

        client.setMobile(clientDTO.getMobile());
        client.setUsername(clientDTO.getUsername());
        client.setPassword(clientDTO.getPassword());
        return client;
    }

    @Override
    public ClientDTO toDomainDTO(Client clientEntity) {
        if (clientEntity == null) {
            return null;
        }

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setJmbg(clientEntity.getJmbg());
        clientDTO.setName(clientEntity.getName());
        clientDTO.setSurname(clientEntity.getSurname());
        clientDTO.setAge(clientEntity.getAge());

        if (clientEntity.getCity() != null) {
            clientDTO.setCity(new CityDTO(clientEntity.getCity().getId(), clientEntity.getCity().getName()));
        }

        clientDTO.setMobile(clientEntity.getMobile());
        clientDTO.setUsername(clientEntity.getUsername());
        clientDTO.setPassword(clientEntity.getPassword());

        return clientDTO;
    }
}