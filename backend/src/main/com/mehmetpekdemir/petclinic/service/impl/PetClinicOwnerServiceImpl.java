package com.mehmetpekdemir.petclinic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mehmetpekdemir.petclinic.dao.OwnerRepository;
import com.mehmetpekdemir.petclinic.dao.PetRepository;
import com.mehmetpekdemir.petclinic.exception.OwnerNotFoundException;
import com.mehmetpekdemir.petclinic.model.Owner;
import com.mehmetpekdemir.petclinic.service.PetClinicOwnerService;

/**
 * Owner için gerekli olan service katmanım(business(iş) katmanım) Buradan
 * Repository(Dao) katmanıma yönlendiriyoruz veritabanı işlemlerinini veritabanı
 * katmanında yapıyoruz. Burada sadece iş kodları bulunmaktadır.
 * 
 * @author MEHMET PEKDEMIR
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PetClinicOwnerServiceImpl implements PetClinicOwnerService {

	private final OwnerRepository ownerRepository;

	private final PetRepository petRepository;

	@Autowired
	public PetClinicOwnerServiceImpl(OwnerRepository ownerRepository, PetRepository petRepository) {
		this.ownerRepository = ownerRepository;
		this.petRepository = petRepository;
	}

	/**
	 * Transactional(readOnly = true) yapılmasının sebebi burada bir trancation
	 * islemi oluşmayacaktır. Veri üzerinde bir değişiklik yapmadığımız için sadece
	 * okunur yaptık.
	 * ROLE_USER veya ROLE_EDITOR kimligine sahip olan herkes bu methoda erişebilmelidir.
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	@Secured(value={"ROLE_USER","ROLE_EDITOR"})
	public List<Owner> findOwners() {
		return ownerRepository.findAll();
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Owner> findOwners(String lastName) {
		return ownerRepository.findByLastName(lastName);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Owner findOwner(Long id) throws OwnerNotFoundException {
		Owner owner = ownerRepository.findById(id);
		if (owner == null)
			throw new OwnerNotFoundException("Owner not found");
		return owner;
	}
	
	@Override
	@CacheEvict(cacheNames = "allOwners",allEntries = true)
	public void createOwner(Owner owner) {
		ownerRepository.createOwner(owner);
	}

	@Override
	@CacheEvict(cacheNames = "allOwners",allEntries = true)
	public void updateOwner(Owner owner) {
		ownerRepository.updateOwner(owner);
	}

	@Override
	@CacheEvict(cacheNames = "allOwners",allEntries = true)
	public void deleteOwner(Long id) {
		petRepository.deleteByOwnerId(id);
		ownerRepository.deleteOwner(id);
	}

}
