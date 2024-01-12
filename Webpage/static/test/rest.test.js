const chai = require('chai');
const chaiHttp = require('chai-http');
const app = require('../rest'); // Adjust the path based on your project structure


chai.use(chaiHttp);
const expect = chai.expect;

describe('Search Endpoint', () => {
  it('should return an array of books when a valid search query is provided', (done) => {
    chai
      .request(app)
      .get('/search?q=example&numitems=5&page=1')
      .end((err, res) => {
        expect(res).to.have.status(200);
        expect(res.body).to.be.an('array');
        expect(res.body).to.have.lengthOf.at.most(5); // Adjust based on your pagination settings
        done();
      });
  });

  it('should return an error when an invalid search query is provided', (done) => {
    chai
      .request(app)
      .get('/search?q=invalid&numitems=5&page=1')
      .end((err, res) => {
        expect(res).to.have.status(500); // Assuming a 500 status code for internal server error
        expect(res.body).to.have.property('error');
        done();
      });
  });

  // Add more test cases as needed

  // Remember to add tests for other endpoints and functionalities
});
