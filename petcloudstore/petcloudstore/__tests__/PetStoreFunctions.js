//sample functions for copilot unit test demo

function sortPetsByNameDescending(pets) {
  //if it returns a negative value, the value in a will be ordered before b.
  //if it returns 0, the ordering of a and b won’t change.
  //if it returns a positive value, the value in b will be ordered before a.
  pets.sort((a, b) => {
    if (a.name > b.name) {
      console.log(`${a.name} is > ${b.name} returning -1`);
      return -1;
    }
    if (a.name < b.name) {
      console.log(`${a.name} is < ${b.name} returning 1`);
      return 1;
    }
    return 0;
  });
}

function countPetsByCategory(pets, category) {
    const counts = {};
  
    pets.forEach(pet => {
      const value = pet[category];
      counts[value] = counts[value] ? counts[value] + 1 : 1;
    });
  
    return counts;
  }

module.exports = {
  sortPetsByNameDescending,
  countPetsByCategory
};