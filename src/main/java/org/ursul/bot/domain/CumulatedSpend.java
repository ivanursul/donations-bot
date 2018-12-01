package org.ursul.bot.domain;

public class CumulatedSpend {

    private Donator donator;
    private double finalSpend;

    public CumulatedSpend(Donator donator, double finalSpend) {
        this.donator = donator;
        this.finalSpend = finalSpend;
    }

    public Donator getDonator() {
        return donator;
    }

    public void setDonator(Donator donator) {
        this.donator = donator;
    }

    public double getFinalSpend() {
        return finalSpend;
    }

    public void setFinalSpend(double finalSpend) {
        this.finalSpend = finalSpend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CumulatedSpend that = (CumulatedSpend) o;

        if (Double.compare(that.finalSpend, finalSpend) != 0) return false;
        return donator != null ? donator.equals(that.donator) : that.donator == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = donator != null ? donator.hashCode() : 0;
        temp = Double.doubleToLongBits(finalSpend);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CumulatedSpend{");
        sb.append("donator=").append(donator);
        sb.append(", finalSpend=").append(finalSpend);
        sb.append('}');
        return sb.toString();
    }
}
